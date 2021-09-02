package org.cascadebot.cascadebot.data.objects

import org.bson.BsonDocument
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.database.BsonObject
import org.cascadebot.cascadebot.utils.GuildDataUtils
import org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode
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

    }

}
