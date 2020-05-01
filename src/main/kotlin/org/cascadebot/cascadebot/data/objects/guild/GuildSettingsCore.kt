package org.cascadebot.cascadebot.data.objects.guild

import com.google.common.collect.Sets
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.objects.Setting
import org.cascadebot.cascadebot.data.objects.SettingsContainer
import org.cascadebot.cascadebot.data.objects.Tag
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.CORE)
class GuildSettingsCore(guildId: Long) {

    private constructor() : this(0L) {
        // Private constructor for MongoDB
    }

    @Setting
    var mentionPrefix = false

    @Setting
    var deleteCommand = true

    @Setting
    var useEmbedForMessages = true

    @Setting
    var showPermErrors = true

    @Setting
    var showModuleErrors = true

    @Setting
    var adminsHaveAllPerms = true

    @Setting
    var allowTagCommands = true

    @Setting
    var helpHideCommandsNoPermission = true

    @Setting
    var helpShowAllModules = false

    @Setting(directlyEditable = false)
    var prefix: String = Config.INS.defaultPrefix

    @Setting(directlyEditable = false)
    private val enabledModules: MutableSet<Module> = Sets.newConcurrentHashSet(Module.getModules(ModuleFlag.DEFAULT))

    @Setting(directlyEditable = false)
    val tags: ConcurrentHashMap<String, Tag> = ConcurrentHashMap()


    //region Modules
    fun enableModule(module: Module): Boolean {
        require(!module.isPrivate) { "This module is not available to be enabled!" }
        return enabledModules.add(module)
    }

    fun disableModule(module: Module): Boolean {
        require(!module.isPrivate) { "This module is not available to be disabled!" }
        require(!module.isRequired) { "Cannot disable the ${module.toString().toLowerCase()} module!" }
        return enabledModules.remove(module)
    }

    fun isModuleEnabled(module: Module): Boolean {
        val isEnabled = enabledModules.contains(module)
        if (!isEnabled && module.isRequired) {
            enabledModules.add(module)
            return true
        }
        return isEnabled
    }

    //endregion


}