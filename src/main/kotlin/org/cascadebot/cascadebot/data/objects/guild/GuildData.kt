package org.cascadebot.cascadebot.data.objects.guild

import de.bild.codec.annotations.Id
import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.ICommandMain
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.managers.CascadeUserDataManager
import org.cascadebot.cascadebot.data.objects.donation.DataFlag
import org.cascadebot.cascadebot.data.objects.donation.Flag
import org.cascadebot.cascadebot.data.objects.donation.FlagContainer
import org.cascadebot.cascadebot.data.objects.donation.Tier
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup
import org.cascadebot.cascadebot.utils.buttons.ButtonsCache
import org.cascadebot.cascadebot.utils.buttons.PersistentButtonGroup
import org.cascadebot.cascadebot.utils.pagination.PageCache
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.set


class GuildData(@field:Id val guildId: Long) {

    private constructor() : this(0L) {
        // Private constructor for MongoDB
    }

    //region Meta information
    private val creationDate = Date()
    //endregion

    private val commandInfo = ConcurrentHashMap<Class<ICommandMain>, MutableSet<GuildCommandInfo>>()
    val guildCommandInfos
        get() = Collections.unmodifiableMap(commandInfo)

    val locale: Locale = Locale.getDefaultLocale()

    //region Guild data containers
    val coreSettings = GuildSettingsCore(guildId)
    val usefulSettings = GuildSettingsUseful()
    val guildModeration = GuildSettingsModeration()
    val guildMusic = GuildSettingsMusic()
    val permissionSettings = GuildPermissions()
    //endregion

    //region Transient fields
    @Transient
    val buttonsCache = ButtonsCache(5)

    @Transient
    val pageCache = PageCache()
    //endregion

    val persistentButtons = HashMap<Long, HashMap<Long, PersistentButtonGroup>>()

    //endregion

    private val supporters: Set<Long> = HashSet()
    private val flags: Set<Flag> = HashSet();

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
        } else coreSettings.isModuleEnabled(command.module)
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

    private fun getGuildTier(): Tier? {
        if (supporters.isEmpty()) {
            return Tier.getTier("default")
        }
        var highest = Tier.getTier("default")
        var highestTierName = "default"
        for (id in supporters) {
            val user = CascadeUserDataManager.getUser(id)
            if (user.blackList.contains(guildId)) {
                continue;
            }
            if (user.tier.isTierParent(highestTierName)) {
                highest = user.tier
                highestTierName = user.tierName
            }
        }
        return highest
    }

    fun getGuildTierName(): String? {
        if (supporters.isEmpty()) {
            return "default"
        }
        var highestTierName = "default"
        for (id in supporters) {
            val user = CascadeUserDataManager.getUser(id)
            if (user.tier.isTierParent(highestTierName)) {
                highestTierName = user.tierName
            }
        }
        return highestTierName
    }

    fun getAllFlags(): FlagContainer {
        val flags = ArrayList(getGuildTier()?.getAllFlags()!!.toList());
        for (guildFlag in this.flags) {
            if (guildFlag !is DataFlag) {
                continue;
            }
            if (getGuildTier()?.hasFlag(guildFlag.id)!!) {
                val compareFlag: Flag? = getGuildTier()?.getFlag(guildFlag.id)
                if (compareFlag !is DataFlag) {
                    continue;
                }
                if (guildFlag > compareFlag) {
                    flags.remove(compareFlag);
                    flags.add(guildFlag);
                }
            } else {
                flags.add(guildFlag)
            }
        }

        for (id in supporters) {
            val user = CascadeUserDataManager.getUser(id)
            if (user.blackList.contains(guildId)) {
                continue;
            }
            for (userFlag in user.flags) {
                if (userFlag !is DataFlag) {
                    continue;
                }
                if (getGuildTier()?.hasFlag(userFlag.id)!!) {
                    val compareFlag: Flag? = getGuildTier()?.getFlag(userFlag.id)
                    if (compareFlag !is DataFlag) {
                        continue;
                    }
                    if (userFlag > compareFlag) {
                        flags.remove(compareFlag);
                        flags.add(userFlag);
                    }
                } else {
                    flags.add(userFlag)
                }
            }
        }

        return FlagContainer(HashSet(flags));
    }

}
