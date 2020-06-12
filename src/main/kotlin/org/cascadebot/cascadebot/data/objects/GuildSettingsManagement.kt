package org.cascadebot.cascadebot.data.objects

import org.cascadebot.cascadebot.commandmeta.Module
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.MANAGEMENT)
class GuildSettingsManagement {

    @Setting
    var allowTagCommands = true

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
        tags[key] = tag
    }

    fun removeTag(key: String): Boolean {
        return tags.remove(key) != null
    }

}