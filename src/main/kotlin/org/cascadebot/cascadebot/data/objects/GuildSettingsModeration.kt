package org.cascadebot.cascadebot.data.objects

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.exception.HttpException
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Icon
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.database.DebugLogCallback
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode
import org.cascadebot.cascadebot.utils.toCapitalized
import java.net.URL
import java.time.Instant
import java.util.Date
import java.util.function.Consumer

@SettingsContainer(module = Module.MODERATION)
class GuildSettingsModeration {

    var writeMode = false

    var modlogChannelNum: Int = 1;

    @Setting
    val purgePinnedMessages: Boolean = false

    @Setting
    private val respectBanOrKickHierarchy = true

    @Setting
    var muteRoleName = "Muted"

    private val objClass = this.javaClass.name

    fun getRespectBanOrKickHierarchy(): Boolean {
        return respectBanOrKickHierarchy
    }

    private val modlogEvents: MutableMap<Long, ChannelModlogEventsInfo> = HashMap()

    fun sendModlogEvent(guildId: Long, modlogEventData: ModlogEventData) {
        val eventsInfo: List<ChannelModlogEventsInfo> = getEventInfoForEvent(modlogEventData.trigger)
        for (eventInfo in eventsInfo) {
            eventInfo.sendEvent(GuildDataManager.getGuildData(guildId), modlogEventData);
        }
        CascadeBot.INS.databaseManager.runAsyncTask { database ->
            database.getCollection(
                "modlog",
                ChannelModlogEventsInfo.MongoModlogEventObject::class.java
            ).insertOne(
                ChannelModlogEventsInfo.MongoModlogEventObject(
                    guildId,
                    modlogEventData,
                    "default" /* TODO change to actual time */
                ), DebugLogCallback("Inserted Event")
            )
        }
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
        return modlogEvents.toMap()
    }

    fun removeModlogEvent(id: Long): ChannelModlogEventsInfo? {
        assertWriteMode()
        return modlogEvents.remove(id)
    }

    fun clearModlogEvents() {
        assertWriteMode()
        return modlogEvents.clear()
    }

    fun buildWebhookClients() {
        for (entry in modlogEvents) {
            entry.value.buildWebhookClient()
        }
    }

    fun enableEvent(channel: TextChannel, event: ModlogEvent): Boolean {
        assertWriteMode()
        if (modlogEvents.containsKey(channel.idLong)) {
            return modlogEvents[channel.idLong]!!.addEvent(event)
        } else {
            createModlogEventsInfo(channel, Consumer { it.addEvent(event) })
            return true
        }
    }

    fun disableEvent(channel: TextChannel, event: ModlogEvent): Boolean {
        assertWriteMode()
        if (modlogEvents.containsKey(channel.idLong)) {
            return modlogEvents[channel.idLong]!!.removeEvent(event)
        } else {
            return false
        }
    }

    fun enableEventByCategory(channel: TextChannel, category: ModlogEvent.Category) {
        assertWriteMode()
        for (modlogEvent in ModlogEvent.getEventsFromCategory(category)) {
            enableEvent(channel, modlogEvent)
        }
    }

    private fun createModlogEventsInfo(channel: TextChannel, consumer: Consumer<ChannelModlogEventsInfo>) {
        assertWriteMode()
        val eventsInfo = ChannelModlogEventsInfo()
        eventsInfo.id = modlogChannelNum++
        modlogEvents.put(channel.idLong, eventsInfo)
        channel.createWebhook(CascadeBot.INS.client.selfUser.name)
            .setAvatar(Icon.from(URL(CascadeBot.INS.client.selfUser.avatarUrl).openStream())).queue { webhook ->
            eventsInfo.webhookId = webhook.idLong
            eventsInfo.webhookToken = webhook.token!!
            eventsInfo.buildWebhookClient()
            consumer.accept(eventsInfo)
        }
    }

    class ChannelModlogEventsInfo {

        private val objClass = this.javaClass.name

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
            assertWriteMode()
            this.webhookId = webhookId
            this.webhookToken = webhookToken
            this.id = id
            buildWebhookClient()
        }

        fun buildWebhookClient() {
            assertWriteMode()
            webhookClient = WebhookClientBuilder(webhookId, webhookToken).build()
        }

        fun getId(): Int {
            return id
        }

        fun getEvents(): List<ModlogEvent> {
            return ArrayList(events)
        }

        fun addEvent(event: ModlogEvent): Boolean {
            assertWriteMode()
            return events.add(event)
        }

        fun removeEvent(event: ModlogEvent): Boolean {
            assertWriteMode()
            return events.remove(event)
        }

        fun getWebhookId(): Long {
            return webhookId
        }

        fun setNewWebhook(webhookId: Long, webhookToken: String) {
            assertWriteMode()
            this.webhookId = webhookId
            this.webhookToken = webhookToken
            buildWebhookClient()
        }

        fun sendEvent(guildData: GuildData, modlogEventData: ModlogEventData) {
            val webhookEmbedBuilder = WebhookEmbedBuilder()

            val path = "enums.modlogevent.${modlogEventData.trigger.name.toLowerCase()}.description"
            val element = Language.getLanguageOrDefault(guildData.locale).getElement(path)

            if (element.isPresent) {
                val affected = when (modlogEventData.trigger.affectedDisplayType) {
                    AffectedDisplayType.NAME -> modlogEventData.affected.name
                    AffectedDisplayType.MENTION -> modlogEventData.affected.mention
                        ?: modlogEventData.affected.name
                }
                webhookEmbedBuilder.setDescription(
                    Language.i18n(
                        guildData.locale,
                        path,
                        affected,
                        modlogEventData.responsible?.asMention
                            ?: Language.i18n(guildData.locale, "words.unknown").toCapitalized(),
                        *modlogEventData.extraDescriptionInfo.toTypedArray()
                    )
                )
            }

            webhookEmbedBuilder.setTitle(
                EmbedTitle(
                    Language.i18n(
                        guildData.locale,
                        "enums.modlogevent." + modlogEventData.trigger.name.toLowerCase() + ".display"
                    ), null
                )
            )
            val affected: ModlogAffected = modlogEventData.affected;

            when (modlogEventData.trigger.displayType) {
                ModlogEvent.ModlogDisplayType.AFFECTED_THUMBNAIL -> {
                    webhookEmbedBuilder.setThumbnailUrl(affected.imageUrl)
                }

                ModlogEvent.ModlogDisplayType.AFFECTED_AUTHOR -> {
                    when (affected.affectedType) {
                        AffectedType.USER -> {
                            webhookEmbedBuilder.setAuthor(
                                WebhookEmbed.EmbedAuthor(
                                    affected.name,
                                    affected.imageUrl,
                                    "https://discord.com/users/" + affected.id
                                )
                            )
                        }

                        AffectedType.EMOTE -> {
                            webhookEmbedBuilder.setAuthor(
                                WebhookEmbed.EmbedAuthor(
                                    affected.name,
                                    affected.imageUrl,
                                    null
                                )
                            )
                        }

                        else -> {
                            webhookEmbedBuilder.setAuthor(WebhookEmbed.EmbedAuthor(affected.name, null, null))
                        }
                    }
                }
            }

            for (embedPart in modlogEventData.extraInfo) {
                embedPart.build(guildData.locale, webhookEmbedBuilder)
            }
            webhookEmbedBuilder.setColor(modlogEventData.trigger.messageType.color.rgb)
            webhookEmbedBuilder.setTimestamp(Instant.now())
            if (modlogEventData.responsible != null) {
                val iconUrl = modlogEventData.responsible.effectiveAvatarUrl
                // TODO: by user
                webhookEmbedBuilder.setFooter(
                    WebhookEmbed.EmbedFooter(
                        modlogEventData.responsible.name + " (" + modlogEventData.responsible.id + ")",
                        iconUrl
                    )
                )
            }
            try {
                webhookClient?.send(
                    WebhookMessageBuilder()
                        .setUsername(CascadeBot.INS.client.selfUser.name)
                        .setAvatarUrl(CascadeBot.INS.client.selfUser.effectiveAvatarUrl)
                        .addEmbeds(webhookEmbedBuilder.build())
                        .build()
                )
            } catch (ignored: HttpException) {
                // TODO not ignore
            }
        }

        class MongoModlogEventObject {

            private var guildId: Long = 0;
            private var modlogEventData: ModlogEventData? = null

            private var createdDate: Date = Date()
            private var tier: String = ""

            private constructor()

            constructor(guildId: Long, modlogEventData: ModlogEventData, tier: String) {
                this.guildId = guildId
                this.modlogEventData = modlogEventData
                this.tier = tier
            }

        }
    }

}
