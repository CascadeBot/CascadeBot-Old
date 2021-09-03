package org.cascadebot.cascadebot.data.objects

import org.bson.BsonDocument
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.database.BsonObject
import org.cascadebot.cascadebot.data.database.DataHandler
import org.cascadebot.cascadebot.utils.GuildDataUtils
import org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode
import org.cascadebot.cascadebot.utils.ifContains
import org.cascadebot.cascadebot.utils.ifContainsArray
import org.cascadebot.cascadebot.utils.ifContainsDocument
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.MANAGEMENT)
class GuildSettingsManagement : BsonObject {

    @Setting
    var allowTagCommands = true

    @Setting
    var displayFilterError = false

    @Setting
    var warnOver10Filters = true

    val tags: ConcurrentHashMap<String, Tag> = ConcurrentHashMap()
    val filters: MutableList<CommandFilter> = mutableListOf()
    val permissions = GuildPermissions()

    val greetings = Greetings()
    val autoRoles: MutableSet<Long> = mutableSetOf()

    fun getTag(key: String): Tag? {
        return tags[key]
    }

    fun hasTag(key: String): Boolean {
        return tags.containsKey(key)
    }

    fun addTag(key: String, tag: Tag) {
        assertWriteMode()
        tags[key] = tag
    }

    fun removeTag(key: String): Boolean {
        assertWriteMode()
        return tags.remove(key) != null
    }

    override fun fromBson(bsonDocument: BsonDocument) {
        bsonDocument.ifContains("allowTagCommands") {
            allowTagCommands = it.asBoolean().value
        }
        bsonDocument.ifContains("displayFilterError") {
            displayFilterError = it.asBoolean().value
        }
        bsonDocument.ifContains("warnOver10Filters") {
            warnOver10Filters = it.asBoolean().value
        }
        bsonDocument.ifContainsDocument("tags") {
            for (entry in it) {
                if (tags.contains(entry.key)) {
                    tags[entry.key]!!.fromBson(entry.value.asDocument())
                } else {
                    val doc = entry.value.asDocument()
                    val tag = Tag(doc["name"]!!.asString().value, doc["content"]!!.asString().value, doc["category"]!!.asString().value)
                    tags[entry.key] = tag
                }
            }
        }
        bsonDocument.ifContainsArray("filters") {
            filters.clear()
            for (bsonFilter in it) {
                val doc = it.asDocument()
                val filer = CommandFilter(doc["name"]!!.asString().value)
                filer.fromBson(doc)
                filters.add(filer)
            }
        }
        bsonDocument.ifContainsDocument("permissions") {
            permissions.fromBson(it)
        }
        bsonDocument.ifContainsDocument("greetings") {
            greetings.fromBson(it)
        }
        bsonDocument.ifContainsArray("autoRoles") {
            autoRoles.clear()
            for (bsonRole in it) {
                autoRoles.add(bsonRole.asNumber().longValue())
            }
        }
    }

    override fun handleRemove(tree: DataHandler.RemovedTree) {
        tree.ifContains("tags") {
            for (entry in tags.entries) {
                it.ifContains(entry.key) {
                    tags.remove(entry.key)
                }
            }
        }
        tree.ifContains("greetings") {
            greetings.handleRemove(it)
        }
    }

}
