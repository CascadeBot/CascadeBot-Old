package org.cascadebot.cascadebot.data.objects.guild

import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.objects.Setting
import org.cascadebot.cascadebot.data.objects.SettingsContainer
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.MANAGEMENT)
class GuildSettingsManagement {

    @Setting
    var allowTagCommands = true

    @Setting(directlyEditable = false)
    val tags: ConcurrentHashMap<String, Tag> = ConcurrentHashMap()

    @Setting(directlyEditable = false)
    val permissions = GuildPermissions()

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