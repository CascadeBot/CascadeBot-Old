package org.cascadebot.cascadebot.data.objects

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.exception.HttpException
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedField
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.Icon
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.database.DebugLogCallback
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Language.i18n
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.moderation.ModlogEvent
import java.net.URL
import java.util.ArrayList
import java.util.Date
import java.util.function.Consumer

@SettingsContainer(module = Module.MODERATION)
class GuildSettingsModeration {

    var guildId: Long = 0

    var modlogChannelNum: Int = 1;

    constructor(guildId: Long) {
        this.guildId = guildId;
    }

    constructor()

    @Setting
    public val purgePinnedMessages: Boolean = false

    private val modlogEvents: MutableMap<Long, ChannelModlogEventsInfo> = HashMap()

    fun sendModlogEvent(modlogEventStore: ModlogEventStore) {
        val eventsInfo: List<ChannelModlogEventsInfo> = getEventInfoForEvent(modlogEventStore.trigger)
        for (eventInfo in eventsInfo) {
            eventInfo.sendEvent(GuildDataManager.getGuildData(guildId), modlogEventStore);
        }
        CascadeBot.INS.databaseManager.runAsyncTask { database -> database.getCollection("modlog", MongoModlogEventObject::class.java).insertOne(MongoModlogEventObject(guildId, modlogEventStore, "default" /* TODO change to actual time */), DebugLogCallback("Inserted Event")) }
    }

    private fun getEventInfoForEvent(event: ModlogEvent): List<ChannelModlogEventsInfo> {
        val channelModlogEventsInfos: MutableList<ChannelModlogEventsInfo> = ArrayList()
        for (entry in modlogEvents) {
            if (entry.value.getEvents().contains(event)) {
                channelModlogEventsInfos.add(entry.value)
            }
        }
        return channelModlogEventsInfos
    }

    fun getModlogEvents(): Map<Long, ChannelModlogEventsInfo> {
        return modlogEvents
    }

    fun buildWebhookClients() {
        for (entry in modlogEvents) {
            entry.value.buildWebhookClient()
        }
    }

    fun enableEvent(channel: TextChannel, event: ModlogEvent): Boolean {
        if (modlogEvents.containsKey(channel.idLong)) {
            return modlogEvents[channel.idLong]!!.addEvent(event)
        } else {
            createModlogEventsInfo(channel, Consumer { it.addEvent(event) })
            return true
        }
    }

    fun disableEvent(channel: TextChannel, event: ModlogEvent): Boolean {
        if (modlogEvents.containsKey(channel.idLong)) {
            return modlogEvents[channel.idLong]!!.removeEvent(event)
        } else {
            return false
        }
    }

    fun enableEventByCategory(channel: TextChannel, category: ModlogEvent.Category) {
        for (modlogEvent in ModlogEvent.getEventsFromCategory(category)) {
            enableEvent(channel, modlogEvent)
        }
    }

    private fun createModlogEventsInfo(channel: TextChannel, consumer: Consumer<ChannelModlogEventsInfo>) {
        val eventsInfo = ChannelModlogEventsInfo()
        eventsInfo.id = modlogChannelNum++
        modlogEvents.put(channel.idLong, eventsInfo)
        channel.createWebhook("Cascade-modlog").setAvatar(Icon.from(URL(CascadeBot.INS.client.selfUser.avatarUrl).openStream())).queue { webhook ->
            eventsInfo.webhookId = webhook.idLong
            eventsInfo.webhookToken = webhook.token!!
            eventsInfo.buildWebhookClient()
            consumer.accept(eventsInfo)
        }
    }

    class ChannelModlogEventsInfo {
        private val events: MutableSet<ModlogEvent> = LinkedHashSet()
        internal var webhookId: Long = 0
        internal var webhookToken: String = ""
        internal var id: Int

        @Transient
        @kotlin.jvm.Transient
        private var webhookClient: WebhookClient? = null

        internal constructor() {
            id = 0
        }

        constructor(webhookId: Long, webhookToken: String, id: Int) {
            this.webhookId = webhookId
            this.webhookToken = webhookToken
            this.id = id
            buildWebhookClient()
        }

        fun buildWebhookClient() {
            webhookClient = WebhookClientBuilder(webhookId, webhookToken).build()
        }

        fun getId(): Int {
            return id
        }

        fun getEvents(): List<ModlogEvent> {
            return ArrayList(events)
        }

        fun addEvent(event: ModlogEvent): Boolean {
            return events.add(event)
        }

        fun removeEvent(event: ModlogEvent): Boolean {
            return events.remove(event)
        }

        fun getWebhookId() : Long {
            return webhookId
        }

        fun setNewWebhook(webhookId: Long, webhookToken: String) {
            this.webhookId = webhookId
            this.webhookToken = webhookToken
            buildWebhookClient()
        }

        fun sendEvent(guildData: GuildData, modlogEventStore: ModlogEventStore) {
            val webhookEmbedBuilder = WebhookEmbedBuilder()
            webhookEmbedBuilder.setTitle(EmbedTitle(i18n(guildData.locale, "enums.moldogevent." + modlogEventStore.trigger.name.toLowerCase() + ".display"), null))
            val affected: ISnowflake = modlogEventStore.affected;
            var affectedType = ""
            val affectedStr = when (affected) {

                is User -> {
                    affectedType = "User";
                    affected.name + " (" + affected.id + ")"
                }
                is Role -> {
                    affectedType = "Role"
                    affected.name + " (" + affected.id + ")"
                }
                is Emote -> {
                    affectedType = "Emote"
                    affected.name
                }
                is Guild -> {
                    affectedType = "Guild"
                    affected.name
                }
                is GuildChannel -> {
                    affectedType = "Channel"
                    affected.name
                }
                else -> null
            }
            if (affectedStr != null) {
                webhookEmbedBuilder.addField(EmbedField(true, "Affected $affectedType", affectedStr))
            }
            for (embedField in modlogEventStore.extraInfo) {
                webhookEmbedBuilder.addField(embedField.getLocalizedEmbedField(guildData.locale))
            }
            webhookEmbedBuilder.setColor(modlogEventStore.trigger.messageType.color.rgb)
            if (modlogEventStore.responsible != null) {
                webhookEmbedBuilder.setFooter(WebhookEmbed.EmbedFooter(modlogEventStore.responsible!!.name + " (" + modlogEventStore.responsible!!.id + ")", null))
            }
            try {
                webhookClient?.send(webhookEmbedBuilder.build())
            } catch (ignored: HttpException) {
                // TODO not ignore
            }
        }
    }
    
    class MongoModlogEventObject {
        
        private var guildId: Long = 0;
        private var modlogEventStore: ModlogEventStore? = null

        private var createdDate: Date = Date()
        private var tier: String = ""
        
        private constructor()
        
        constructor(guildId: Long, modlogEventStore: ModlogEventStore, tier: String) {
            this.guildId = guildId
            this.modlogEventStore = modlogEventStore
            this.tier = tier
        }
        
    }
}
