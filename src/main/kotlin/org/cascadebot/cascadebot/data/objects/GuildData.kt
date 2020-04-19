package org.cascadebot.cascadebot.data.objects

import com.google.common.collect.Sets
import de.bild.codec.annotations.Id
import de.bild.codec.annotations.PreSave
import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.ICommandMain
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup
import org.cascadebot.cascadebot.utils.buttons.ButtonsCache
import org.cascadebot.cascadebot.utils.buttons.PersistentButtonGroup
import org.cascadebot.cascadebot.utils.pagination.PageCache
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

class GuildData(@field:Id private val guildId: Long) {

    constructor() : this(0L) {
        // Private constructor for MongoDB
    }

    //region Meta information
    private val creationDate = Date()
    //endregion

    private val commandInfo = ConcurrentHashMap<Class<ICommandMain>, GuildCommandInfo>()
    val guildCommandInfos: Map<Class<ICommandMain>, GuildCommandInfo>
        get() = Collections.unmodifiableMap(commandInfo)

    private val enabledFlags: MutableSet<Flag> = Sets.newConcurrentHashSet()

    val locale: Locale = Locale.getDefaultLocale()

    //region Guild data containers
    val coreSettings = GuildSettingsCore(guildId)
    val usefulSettings = GuildSettingsUseful()
    val guildModeration = GuildSettingsModeration()
    val guildMusic = GuildSettingsMusic()
    val permissions = GuildPermissions()
    //endregion

    //region Transient fields
    @Transient
    val buttonsCache = ButtonsCache(5)

    @Transient
    val pageCache = PageCache()
    //endregion

    val persistentButtons = HashMap<Long, HashMap<Long, PersistentButtonGroup>?>()

    //endregion
    //region Data Loaded Methods
    fun onGuildLoaded() {
        loadMusicSettings()
    }

    private fun loadMusicSettings() {
        val player = CascadeBot.INS.musicHandler.getPlayer(guildId)!!
        if (guildMusic.preserveVolume) {
            player.volume = guildMusic.volume
        }
        if (guildMusic.preserveEqualizer) {
            if (CascadeBot.INS.musicHandler.lavalinkEnabled) {
                if (player is CascadeLavalinkPlayer) {
                    player.setBands(guildMusic.equalizerBands)
                }
            }
        }
    }

    //region Commands
    fun enableCommand(command: ICommandMain) {
        if (command.module.isPrivate) return
        if (commandInfo.contains(command.javaClass) || !command.module.isDefault) {
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
            commandInfo[command.javaClass]!!.enabled
        } else command.module.isDefault
    }

    fun getCommandAliases(command: ICommandMain): Set<String> {
        return if (commandInfo.contains(command.javaClass)) {
            commandInfo[command.javaClass]!!.aliases
        } else command.getGlobalAliases(locale)
    }

    fun addAlias(command: ICommandMain, alias: String): Boolean {
        return getGuildCommandInfo(command).aliases.add(alias)
    }

    fun removeAlias(command: ICommandMain, alias: String): Boolean {
        return getGuildCommandInfo(command).aliases.remove(alias)
    }

    @BsonIgnore
    private fun getGuildCommandInfo(command: ICommandMain): GuildCommandInfo {
        return commandInfo.computeIfAbsent(command.javaClass) { GuildCommandInfo(command, locale) }
    }

    //endregion
    fun enableFlag(flag: Flag): Boolean {
        return enabledFlags.add(flag)
    }

    fun disableFlag(flag: Flag): Boolean {
        return enabledFlags.remove(flag)
    }

    fun isFlagEnabled(flag: Flag): Boolean {
        return enabledFlags.contains(flag)
    }

    fun addButtonGroup(channel: MessageChannel, message: Message, group: ButtonGroup) {
        group.setMessage(message.idLong)
        if (group is PersistentButtonGroup) {
            putPersistentButtonGroup(channel.idLong, message.idLong, group)
        } else {
            buttonsCache.put(channel.idLong, message.idLong, group)
        }
    }

    private fun putPersistentButtonGroup(channelId: Long, messageId: Long, buttonGroup: PersistentButtonGroup) {
        if (persistentButtons.containsKey(channelId) && persistentButtons[channelId] != null) {
            persistentButtons[channelId]!![messageId] = buttonGroup
        } else {
            persistentButtons[channelId] = HashMap()
            persistentButtons[channelId]!![messageId] = buttonGroup
        }
    }

    //endregion


}
