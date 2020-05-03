package org.cascadebot.cascadebot.data.objects

import com.google.common.collect.Sets
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.ICommandMain
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.language.Locale
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.CORE)
class GuildSettingsCore {

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
    var helpHideCommandsNoPermission = true

    @Setting
    var helpShowAllModules = false

    var locale: Locale = Locale.getDefaultLocale()

    @Setting(directlyEditable = false)
    private val commandInfo = ConcurrentHashMap<Class<ICommandMain>, MutableSet<GuildCommandInfo>>()

    @Setting(directlyEditable = false)
    var prefix: String = Config.INS.defaultPrefix

    @Setting(directlyEditable = false)
    private val enabledModules: MutableSet<Module> = Sets.newConcurrentHashSet(Module.getModules(ModuleFlag.DEFAULT))

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

    //region Commands
    fun enableCommand(command: ICommandMain) {
        if (command.module.isPrivate) return
        if (commandInfo.contains(command.javaClass)) {
            getGuildCommandInfo(command).enabled = true
        }
    }

    fun enableCommandByModule(module: Module) {
        if (module.isPrivate) return
        for (command in CascadeBot.INS.commandManager.getCommandsByModule(module)) {
            enableCommand(command)
        }
    }

    fun disableCommand(command: ICommandMain) {
        if (command.module.isPrivate) return
        getGuildCommandInfo(command).enabled = false
    }

    fun disableCommandByModule(module: Module) {
        if (module.isPrivate) return
        for (command in CascadeBot.INS.commandManager.getCommandsByModule(module)) {
            disableCommand(command)
        }
    }

    fun isCommandEnabled(command: ICommandMain): Boolean {
        return if (commandInfo.contains(command.javaClass)) {
            getGuildCommandInfo(command).enabled
        } else isModuleEnabled(command.module)
    }

    fun getCommandAliases(command: ICommandMain): Set<String> {
        return if (commandInfo.contains(command.javaClass)) {
            getGuildCommandInfo(command).aliases.applyChanges(command.getGlobalAliases(locale))
        } else command.getGlobalAliases(locale)
    }

    fun addAlias(command: ICommandMain, alias: String): Boolean {
        return getGuildCommandInfo(command).aliases.add(alias)
    }

    fun removeAlias(command: ICommandMain, alias: String): Boolean {
        return getGuildCommandInfo(command).aliases.remove(alias)
    }

    private fun getGuildCommandInfoSet(command: ICommandMain): MutableSet<GuildCommandInfo> {
        return commandInfo.computeIfAbsent(command.javaClass) {
            mutableSetOf(GuildCommandInfo(command, locale))
        }
    }

    private fun getGuildCommandInfo(command: ICommandMain, locale: Locale = this.locale): GuildCommandInfo {
        return getGuildCommandInfoSet(command).find { it.locale == locale } ?: run {
            val commandInfo = GuildCommandInfo(command, locale)
            getGuildCommandInfoSet(command).add(commandInfo)
            commandInfo
        }
    }

    //endregion


}