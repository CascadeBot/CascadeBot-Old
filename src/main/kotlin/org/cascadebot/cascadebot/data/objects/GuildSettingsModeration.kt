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
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.moderation.ModlogEvent
import java.util.ArrayList
import java.util.Collections
import java.util.function.Consumer

@SettingsContainer(module = Module.MODERATION)
class GuildSettingsModeration() {

    @Setting
    public val purgePinnedMessages: Boolean = false

    private val modlogEvents: MutableList<ChannelModlogEventsInfo> = ArrayList()

    fun sendModlogEvent(modlogEventStore: ModlogEventStore) {
        val eventsInfo: List<ChannelModlogEventsInfo> = getEventInfoForEvent(modlogEventStore.trigger)
        for (eventInfo in eventsInfo) {
            eventInfo.sendEvent(modlogEventStore);
        }
        // TODO add to list for dashboard
    }

    private fun getEventInfoForEvent(event: ModlogEvent): List<ChannelModlogEventsInfo> {
        val channelModlogEventsInfos: MutableList<ChannelModlogEventsInfo> = ArrayList()
        for (channelModlogEventsInfo in modlogEvents) {
            if (channelModlogEventsInfo.getEvents().contains(event)) {
                channelModlogEventsInfos.add(channelModlogEventsInfo)
            }
        }
        return channelModlogEventsInfos
    }

    fun getModlogEvents(): List<ChannelModlogEventsInfo> {
        return modlogEvents
    }

    fun buildWebhookClients() {
        for (channelModlogEventsInfo in modlogEvents) {
            channelModlogEventsInfo.buildWebhookClient()
        }
    }

    fun createModlogEventsInfo(channel: TextChannel, consumer: Consumer<ChannelModlogEventsInfo>) {
        channel.createWebhook("Cascade-modlog").queue { webhook ->
            val eventsInfo = ChannelModlogEventsInfo(channel.idLong, webhook.idLong, webhook.token!!)
            modlogEvents.add(eventsInfo)
            consumer.accept(eventsInfo)
        }
    }

    class ChannelModlogEventsInfo {
        private val events: MutableList<ModlogEvent> = ArrayList()
        private var channelId: Long = 0
        private var webhookId: Long = 0
        private var webhookToken: String = ""

        @Transient
        @kotlin.jvm.Transient
        private var webhookClient: WebhookClient? = null

        private constructor() {}
        constructor(channelId: Long, webhookId: Long, webhookToken: String) {
            this.channelId = channelId
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

        fun sendEvent(modlogEventStore: ModlogEventStore) {
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
                webhookEmbedBuilder.addField(embedField)
            }
            webhookEmbedBuilder.setColor(modlogEventStore.trigger.messageType.color.rgb)
            if (modlogEventStore.responsible != null) {
                webhookEmbedBuilder.setFooter(WebhookEmbed.EmbedFooter(modlogEventStore.responsible.name + " (" + modlogEventStore.responsible.id + ")", null))
            }
            webhookClient?.send(webhookEmbedBuilder.build());
        }
    }
}
