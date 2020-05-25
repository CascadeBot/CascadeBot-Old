package org.cascadebot.cascadebot.data.objects

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedField
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.Icon
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.moderation.ModlogEvent
import java.net.URL
import java.util.ArrayList
import java.util.Collections
import java.util.function.Consumer

@SettingsContainer(module = Module.MODERATION)
class GuildSettingsModeration {

    var guildData: GuildData? = null

    constructor(guildData: GuildData) {
        this.guildData = guildData
    }

    constructor()

    @Setting
    public val purgePinnedMessages: Boolean = false

    private val modlogEvents: MutableMap<Long, ChannelModlogEventsInfo> = HashMap()

    fun sendModlogEvent(modlogEventStore: ModlogEventStore) {
        val eventsInfo: List<ChannelModlogEventsInfo> = getEventInfoForEvent(modlogEventStore.trigger)
        for (eventInfo in eventsInfo) {
            guildData?.let { eventInfo.sendEvent(it, modlogEventStore) };
        }
        // TODO add to list for dashboard
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

    fun getModlogEvents(): List<ChannelModlogEventsInfo> {
        return modlogEvents.map { entry -> entry.value }
    }

    fun buildWebhookClients() {
        for (entry in modlogEvents) {
            entry.value.buildWebhookClient()
        }
    }

    fun enableEvent(channel: TextChannel, event: ModlogEvent) {
        if (modlogEvents.containsKey(channel.idLong)) {
            modlogEvents[channel.idLong]?.addEvent(event)
        } else {
            createModlogEventsInfo(channel, Consumer { it.addEvent(event) })
        }
    }

    fun enableEventByCategory(channel: TextChannel, category: ModlogEvent.Category) {
        for (modlogEvent in ModlogEvent.getEventsFromCategory(category)) {
            enableEvent(channel, modlogEvent)
        }
    }

    private fun createModlogEventsInfo(channel: TextChannel, consumer: Consumer<ChannelModlogEventsInfo>) {
        channel.createWebhook("Cascade-modlog").setAvatar(Icon.from(URL(CascadeBot.INS.client.selfUser.avatarUrl).openStream())).queue { webhook ->
            val eventsInfo = ChannelModlogEventsInfo(webhook.idLong, webhook.token!!)
            modlogEvents.put(channel.idLong, eventsInfo)
            consumer.accept(eventsInfo)
        }
    }

    class ChannelModlogEventsInfo {
        private val events: MutableList<ModlogEvent> = ArrayList()
        private var webhookId: Long = 0
        private var webhookToken: String = ""

        @Transient
        @kotlin.jvm.Transient
        private var webhookClient: WebhookClient? = null

        private constructor() {}
        constructor(webhookId: Long, webhookToken: String) {
            this.webhookId = webhookId
            this.webhookToken = webhookToken
            buildWebhookClient()
        }

        fun buildWebhookClient() {
            webhookClient = WebhookClientBuilder(webhookId, webhookToken).build()
        }

        fun getEvents(): List<ModlogEvent> {
            return Collections.unmodifiableList(events)
        }

        fun addEvent(event: ModlogEvent) {
            events.add(event)
        }

        fun removeEvent(event: ModlogEvent) {
            events.remove(event)
        }

        fun sendEvent(guildData: GuildData, modlogEventStore: ModlogEventStore) {
            val webhookEmbedBuilder = WebhookEmbedBuilder()
            webhookEmbedBuilder.setTitle(EmbedTitle(modlogEventStore.trigger.name, null))
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
                    null
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
                webhookEmbedBuilder.setFooter(WebhookEmbed.EmbedFooter(modlogEventStore.responsible.name + " (" + modlogEventStore.responsible.id + ")", null))
            }
            webhookClient?.send(webhookEmbedBuilder.build());
        }
    }
}
