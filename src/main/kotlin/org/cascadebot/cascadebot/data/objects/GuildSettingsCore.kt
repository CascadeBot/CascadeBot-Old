package org.cascadebot.cascadebot.data.objects

import com.google.common.collect.Sets
import org.bson.BsonDocument
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.database.BsonObject
import org.cascadebot.cascadebot.data.database.DataHandler
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode
import org.cascadebot.cascadebot.utils.ifContains
import org.cascadebot.cascadebot.utils.ifContainsArray
import org.cascadebot.cascadebot.utils.ifContainsBoolean
import org.cascadebot.cascadebot.utils.ifContainsString
import java.lang.UnsupportedOperationException
import java.util.concurrent.ConcurrentHashMap


@SettingsContainer(module = Module.CORE)
class GuildSettingsCore : BsonObject {

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
    var prefix: String = Config.INS.defaultPrefix

    private val commandInfo = ConcurrentHashMap<Class<MainCommand>, MutableSet<GuildCommandInfo>>()
    private val enabledModules: MutableSet<Module> = Sets.newConcurrentHashSet(Module.getModules(ModuleFlag.DEFAULT))

    

    //region Modules
    fun enableModule(module: Module): Boolean {
        assertWriteMode()
        require(!module.isPrivate) { "This module is not available to be enabled!" }
        return enabledModules.add(module)
    }

    fun disableModule(module: Module): Boolean {
        assertWriteMode()
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
    fun enableCommand(command: MainCommand) {
        assertWriteMode()
        if (command.module().isPrivate) return
        if (commandInfo.contains(command.javaClass)) {
            getGuildCommandInfo(command).enabled = true
        }
    }

    fun enableCommandByModule(module: Module) {
        assertWriteMode()
        if (module.isPrivate) return
        for (command in CascadeBot.INS.commandManager.getCommandsByModule(module)) {
            enableCommand(command)
        }
    }

    fun disableCommand(command: MainCommand) {
        assertWriteMode()
        if (command.module().isPrivate) return
        getGuildCommandInfo(command).enabled = false
    }

    fun disableCommandByModule(module: Module) {
        assertWriteMode()
        if (module.isPrivate) return
        for (command in CascadeBot.INS.commandManager.getCommandsByModule(module)) {
            disableCommand(command)
        }
    }

    fun isCommandEnabled(command: MainCommand): Boolean {
        return if (commandInfo.contains(command.javaClass)) {
            getGuildCommandInfo(command).enabled
        } else isModuleEnabled(command.module())
    }

    fun getCommandAliases(command: MainCommand): Set<String> {
        return if (commandInfo.contains(command.javaClass)) {
            getGuildCommandInfo(command).aliases.applyChanges(command.globalAliases(locale))
        } else command.globalAliases(locale)
    }

    fun addAlias(command: MainCommand, alias: String): Boolean {
        assertWriteMode()
        return getGuildCommandInfo(command).aliases.add(alias)
    }

    fun removeAlias(command: MainCommand, alias: String): Boolean {
        assertWriteMode()
        return getGuildCommandInfo(command).aliases.remove(alias)
    }

    private fun getGuildCommandInfoSet(command: MainCommand): MutableSet<GuildCommandInfo> {
        return commandInfo.computeIfAbsent(command.javaClass) {
            mutableSetOf(GuildCommandInfo(command, locale))
        }
    }

    private fun getGuildCommandInfo(command: MainCommand, locale: Locale = this.locale): GuildCommandInfo {
        return getGuildCommandInfoSet(command).find { it.locale == locale } ?: run {
            val commandInfo = GuildCommandInfo(command, locale)
            getGuildCommandInfoSet(command).add(commandInfo)
            commandInfo
        }
    }

    //endregion

    override fun fromBson(bsonDocument: BsonDocument) {
        bsonDocument.ifContainsBoolean("mentionPrefix") { mentionPrefix = it }
        bsonDocument.ifContainsBoolean("deleteCommand") { deleteCommand = it }
        bsonDocument.ifContainsBoolean("useEmbedForMessages") { useEmbedForMessages = it }
        bsonDocument.ifContainsBoolean("showPermErrors") { showPermErrors = it }
        bsonDocument.ifContainsBoolean("showModuleErrors") { showModuleErrors = it }
        bsonDocument.ifContainsBoolean("adminsHaveAllPerms") { adminsHaveAllPerms = it }
        bsonDocument.ifContainsBoolean("helpHideCommandsNoPermission") { helpHideCommandsNoPermission = it }
        bsonDocument.ifContainsBoolean("helpShowAllModules") { helpShowAllModules = it }
        bsonDocument.ifContainsString("locale") { locale = Locale.valueOf(it) }
        bsonDocument.ifContainsString("prefix") { prefix = it }
        bsonDocument.ifContainsArray("enabledModules") { array ->
            enabledModules.clear();
            array.map{ it.asString().value } // Convert each array value to a string
                .map { string -> Module.valueOf(string) } // Map each value to a Module enum
                .forEach { enabledModules.add(it) }
        }
    }

    override fun handleRemove(tree: DataHandler.RemovedTree) {

    }

}