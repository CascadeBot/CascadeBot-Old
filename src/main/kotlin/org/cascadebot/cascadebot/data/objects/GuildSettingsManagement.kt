package org.cascadebot.cascadebot.data.objects

import org.cascadebot.cascadebot.commandmeta.Module
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.MANAGEMENT)
class GuildSettingsManagement {

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

    var writeMode = false

    fun getTag(key: String): Tag? {
        return tags[key]
    }

    fun hasTag(key: String): Boolean {
        return tags.containsKey(key)
    }

    fun addTag(key: String, tag: Tag) {
        if (!writeMode) throw UnsupportedOperationException("Cannot modify Guild data if not in write mode!")
        tags[key] = tag
    }

    fun removeTag(key: String): Boolean {
        if (!writeMode) throw UnsupportedOperationException("Cannot modify Guild data if not in write mode!")
        return tags.remove(key) != null
    }

}