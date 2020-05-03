package org.cascadebot.cascadebot.data.objects

import com.google.common.collect.Sets
import de.bild.codec.annotations.Id
import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.ICommandMain
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup
import org.cascadebot.cascadebot.utils.buttons.ButtonsCache
import org.cascadebot.cascadebot.utils.buttons.PersistentButtonGroup
import org.cascadebot.cascadebot.utils.pagination.PageCache
import java.util.Collections
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

class GuildData(@field:Id val guildId: Long) {

    private constructor() : this(0L) {
        // Private constructor for MongoDB
    }

    //region Meta information
    private val creationDate = Date()
    //endregion

    val enabledFlags: MutableSet<Flag> = Sets.newConcurrentHashSet()

    // TODO: Keep this here or remove it? It just serves as an accessor for the actual locale
    val locale: Locale
        get() = coreSettings.locale

    //region Guild data containers
    val coreSettings = GuildSettingsCore()
    val usefulSettings = GuildSettingsUseful()
    val moderationSettings = GuildSettingsModeration()
    val managementSettings = GuildSettingsManagement()
    val musicSettings = GuildSettingsMusic()
    //endregion

    //region Transient fields
    @Transient
    val buttonsCache = ButtonsCache(5)

    @Transient
    val pageCache = PageCache()
    //endregion

    val persistentButtons = HashMap<Long, HashMap<Long, PersistentButtonGroup>>()

    //endregion
    //region Data Loaded Methods
    fun onGuildLoaded() {
        loadMusicSettings()
    }

    private fun loadMusicSettings() {
        val player = CascadeBot.INS.musicHandler.getPlayer(guildId)!!
        if (musicSettings.preserveVolume) {
            player.volume = musicSettings.volume
        }
        if (musicSettings.preserveEqualizer) {
            if (CascadeBot.INS.musicHandler.lavalinkEnabled) {
                if (player is CascadeLavalinkPlayer) {
                    player.setBands(musicSettings.equalizerBands)
                }
            }
        }
    }

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
