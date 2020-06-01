package org.cascadebot.cascadebot.utils

import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Message.MessageFlag
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageType
import java.time.OffsetDateTime
import java.util.stream.Collectors

class SerializableMessage(
        var id: Long,
        var channelId: Long,
        var guildId: Long,
        var authorId: Long,
        var content: String,
        var sent: OffsetDateTime,
        var edited: OffsetDateTime?,
        var tts: Boolean,
        var mentionsEveryone: Boolean,
        var userMentions: List<Long>,
        var channelMentions: List<ChannelMention>,
        var roleMentions: List<Long>,
        var attachments: List<Attachment>,
        var embeds: List<WebhookEmbed>,
        var reactions: List<Reaction>,
        var pinned: Boolean,
        var messageType: MessageType,
        var messageFlags: List<MessageFlag>?
) {
    companion object {
        @JvmStatic
        fun createSerializeMessageFromJda(message: Message): SerializableMessage? {
            val userMentions: List<Long> = message.mentionedUsers.stream().map { it.idLong }.collect(Collectors.toList())
            val channelMentions: List<ChannelMention> = message.mentionedChannels.stream().map { ChannelMention(it.idLong, it.guild.idLong) }.collect(Collectors.toList())
            val roleMentions: List<Long> = message.mentionedRoles.stream().map { it.idLong }.collect(Collectors.toList())
            val attachments: List<Attachment> = message.attachments.stream().map { Attachment(it.idLong, it.fileName, it.size, it.url) }.collect(Collectors.toList())
            val embeds: List<WebhookEmbed> = message.embeds.stream().map {
                var webhookEmbedBuilder = WebhookEmbedBuilder()
                if (it.title != null) {
                    webhookEmbedBuilder.setTitle(WebhookEmbed.EmbedTitle(it.title!!, it.url))
                }
                if (it.description != null) {
                    webhookEmbedBuilder.setDescription(it.description)
                }
                if (it.timestamp != null) {
                    webhookEmbedBuilder.setTimestamp(it.timestamp)
                }
                webhookEmbedBuilder.setColor(it.colorRaw)
                if (it.footer != null) {
                    webhookEmbedBuilder.setFooter(WebhookEmbed.EmbedFooter(it.footer!!.text!!, it.footer!!.iconUrl))
                }
                if (it.image != null) {
                    webhookEmbedBuilder.setImageUrl(it.image!!.url)
                }
                if (it.thumbnail != null) {
                    webhookEmbedBuilder.setThumbnailUrl(it.thumbnail!!.url)
                }
                if (it.author != null) {
                    webhookEmbedBuilder.setAuthor(WebhookEmbed.EmbedAuthor(it.author!!.name!!, it.author!!.iconUrl, it.author!!.url))
                }
                for (field: MessageEmbed.Field in it.fields) {
                    webhookEmbedBuilder.addField(WebhookEmbed.EmbedField(field.isInline, field.name!!, field.value!!))
                }
                webhookEmbedBuilder.build()
            }.collect(Collectors.toList())
            val reactions: List<Reaction> = message.reactions.stream().map { Reaction(it.count, it.reactionEmote.idLong, it.reactionEmote.name) }.collect(Collectors.toList())
            return SerializableMessage(message.idLong, message.channel.idLong, message.guild.idLong, message.author.idLong,
                    message.contentRaw, message.timeCreated, message.timeEdited, message.isTTS, message.mentionsEveryone(),
                    userMentions, channelMentions, roleMentions, attachments, embeds, reactions, message.isPinned, message.type,
                    message.flags.stream().collect(Collectors.toList()));
        }
    }
}

data class Attachment(
        var id: Long,
        var fileName: String,
        var size: Int,
        var url: String
)

data class ChannelMention(
        var id: Long,
        var guildId: Long
)

data class Reaction(
        var count: Int,
        var id: Long?,
        var name: String
)