package org.cascadebot.cascadebot.data.objects

import com.google.common.collect.Sets
import de.bild.codec.annotations.Id
import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.utils.interactions.PersistentComponent
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow
import org.cascadebot.cascadebot.utils.interactions.InteractionCache
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer
import org.cascadebot.cascadebot.utils.pagination.PageCache
import org.cascadebot.cascadebot.utils.votes.VoteGroup
import java.util.Date

class GuildData(@field:Id val guildId: Long) {

    private constructor() : this(0L) {
        // Private constructor for MongoDB
    }

    //region Meta information
    private val creationDate = Date()
    //endregion

    val enabledFlags: MutableSet<Flag> = Sets.newConcurrentHashSet()

    // This is a cache of the muted role so we can use the same one each time if, for
    // some reason, there are multiple "Muted" roles
    var mutedRoleId: Long = -1

    // TODO: Keep this here or remove it? It just serves as an accessor for the actual locale
    val locale: Locale
        get() = core.locale

    //region Guild data containers
    val core = GuildSettingsCore()
    val useful = GuildSettingsUseful()
    val moderation = GuildSettingsModeration()
    val management = GuildSettingsManagement()
    val music = GuildSettingsMusic()
    //endregion

    //region Transient fields
    @Transient
    val componentCache = InteractionCache(5)

    @Transient
    val pageCache = PageCache()

    @Transient
    val permissionsManager = PerGuildPermissionsManager()
    //endregion

    val persistentComponents = HashMap<Long, HashMap<Long, List<List<PersistentComponent>>>>()

    val voteGroups: MutableMap<String, VoteGroup> = mutableMapOf()

    //endregion

    //region Data Loaded Methods
    fun onGuildLoaded() {
        loadComponents()
        permissionsManager.registerPermissions(this)
    }

    private fun loadComponents() {
        for (channelEntry in persistentComponents.entries) {
            val channelId = channelEntry.key
            for(messageEntry in channelEntry.value.entries) {
                val messageId = messageEntry.key
                val container = ComponentContainer()
                for (storedRow in messageEntry.value) {
                    val row = CascadeActionRow()
                    for (storedComp in storedRow) {
                        row.addComponent(storedComp.component)
                    }
                    container.addRow(row)
                }
                componentCache.put(channelId, messageId, container)
            }
        }
    }
    //endregion

    //region Flags
    fun enableFlag(flag: Flag): Boolean {
        return enabledFlags.add(flag)
    }

    fun disableFlag(flag: Flag): Boolean {
        return enabledFlags.remove(flag)
    }

    fun isFlagEnabled(flag: Flag): Boolean {
        return enabledFlags.contains(flag)
    }
    //endregion

    //region Components
    fun addComponents(channel: MessageChannel, message: Message, container: ComponentContainer) {
        if (container.persistent) {
            val containerList: MutableList<List<PersistentComponent>> = mutableListOf()
            for (row in container.getComponents()) {
                val rowList: MutableList<PersistentComponent> = mutableListOf()
                for (component in row.getComponents()) {
                    rowList.add(PersistentComponent.values().find { it.component == component }!!)
                }
                containerList.add(rowList.toList())
            }
            putPersistentComponents(channel.idLong, message.idLong, containerList.toList())
        }
        componentCache.put(channel.idLong, message.idLong, container)
    }

    private fun putPersistentComponents(channelId: Long, messageId: Long, persistentComponentList: List<List<PersistentComponent>>) {
        if (persistentComponents.containsKey(channelId) && persistentComponents[channelId] != null) {
            persistentComponents[channelId]!![messageId] = persistentComponentList
        } else {
            persistentComponents[channelId] = HashMap()
            persistentComponents[channelId]!![messageId] = persistentComponentList
        }
    }

    //endregion

    fun findVoteGroupByMessageAndChannel(channelId: Long, messageId: Long): VoteGroup? {
        return voteGroups.entries.map { it.value }.find { it.channelId == channelId && it.messageId == messageId }
    }


}
