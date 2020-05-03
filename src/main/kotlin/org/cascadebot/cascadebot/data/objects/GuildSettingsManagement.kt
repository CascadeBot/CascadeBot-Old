package org.cascadebot.cascadebot.data.objects

import org.cascadebot.cascadebot.commandmeta.Module
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.MANAGEMENT)
class GuildSettingsManagement {

    @Setting
    var allowTagCommands = true

    @Setting(directlyEditable = false)
    val tags: ConcurrentHashMap<String, Tag> = ConcurrentHashMap()

    @Setting(directlyEditable = false)
    val permissions = GuildPermissions()

}