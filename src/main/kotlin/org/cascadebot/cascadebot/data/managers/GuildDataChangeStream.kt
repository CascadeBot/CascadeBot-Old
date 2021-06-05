/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.managers

import com.mongodb.async.client.MongoDatabase
import com.mongodb.client.model.changestream.ChangeStreamDocument
import org.bson.BsonType
import org.bson.BsonValue
import org.bson.codecs.DecoderContext
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.database.DatabaseManager
import org.cascadebot.cascadebot.data.objects.GuildData
import org.springframework.data.mongodb.util.BsonUtils
import java.lang.reflect.Field
import java.lang.reflect.Modifier

object GuildDataChangeStream {

    @JvmStatic
    fun setupChangeStreams(databaseManager: DatabaseManager) {
        databaseManager.runAsyncTask { database: MongoDatabase ->
            val changeStreamIterable =
                database.getCollection(GuildDataManager.COLLECTION, GuildData::class.java).watch()
            changeStreamIterable.forEach({ guildDataChangeStreamDocument: ChangeStreamDocument<GuildData?> ->
                if (guildDataChangeStreamDocument.fullDocument != null) {
                    GuildDataManager.replaceInternal(guildDataChangeStreamDocument.fullDocument)
                } else {
                    val currentData = GuildDataManager.getGuildData(
                        guildDataChangeStreamDocument.documentKey["_id"]!!.asNumber().longValue()
                    )
                    val updateDescription = guildDataChangeStreamDocument.updateDescription
                    if (updateDescription.updatedFields != null) {
                        for ((key, value) in updateDescription.updatedFields) {
                            try {
                                if (!updateGuildData(key, currentData, bsonValueToJava(databaseManager, value))) break
                            } catch (e: ClassNotFoundException) {
                                CascadeBot.LOGGER.error("Failed to update data", e)
                                break
                            }
                        }
                    }
                    if (updateDescription.removedFields != null) {
                        for (removed in updateDescription.removedFields) {
                            if (!updateGuildData(removed, currentData, null)) break
                        }
                    }
                }
            }) { _: Void?, throwable: Throwable? ->
                if (throwable != null) {
                    CascadeBot.LOGGER.error("Error on change stream", throwable)
                }
            }
        }

        CascadeBot.LOGGER.info("Watcher setup")
    }

    @Throws(ClassNotFoundException::class)
    fun bsonValueToJava(databaseManager: DatabaseManager, bsonValue: BsonValue): Any {
        return if (bsonValue.bsonType == BsonType.ARRAY) {
            val list = ArrayList<Any>()
            for (arrayValue in bsonValue.asArray().values) {
                list.add(bsonValueToJava(databaseManager, arrayValue))
            }
        } else if (bsonValue.bsonType == BsonType.DOCUMENT) {
            if (bsonValue.asDocument().containsKey("objClass")) {
                databaseManager.codecRegistry
                    .get(Class.forName(bsonValue.asDocument().getString("objClass").value)).decode(
                        bsonValue.asDocument().asBsonReader(),
                        DecoderContext.builder().build()
                    ) // TODO this can work if we can find the java class, maybe store it?
            } else {
                CascadeBot.LOGGER.error("Data object doesn't contain object class, so we can't properly decode it!")
                bsonValue
            }
        } else {
            // This only handles primitives basically
            BsonUtils.toJavaType(bsonValue)
        }
    }

    private fun updateGuildData(path: String, guildData: GuildData, newValue: Any?): Boolean {
        val split = path.split("\\.".toRegex()).toTypedArray()
        val last = split[split.size - 1]
        var current: Any = guildData
        for (part in split.copyOfRange(0, split.size - 1)) {
            try {
                val field = current.javaClass.getDeclaredField(part)
                field.isAccessible = true
                current = field[current]
            } catch (e: NoSuchFieldException) {
                CascadeBot.LOGGER.error("Failed to update guild data", e)
                return false
            } catch (e: IllegalAccessException) {
                CascadeBot.LOGGER.error("Failed to update guild data", e)
                return false
            }
        }
        try {
            val field = current.javaClass.getDeclaredField(last)
            field.isAccessible = true
            val modifiersField = Field::class.java.getDeclaredField("modifiers")
            modifiersField.isAccessible = true
            modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
            field[current] = newValue
        } catch (e: NoSuchFieldException) {
            CascadeBot.LOGGER.error("Failed to update guild data", e)
            return false
        } catch (e: IllegalAccessException) {
            CascadeBot.LOGGER.error("Failed to update guild data", e)
            return false
        }
        return true
    }


}