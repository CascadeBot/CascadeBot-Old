package org.cascadebot.cascadebot.messaging

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.internal.utils.Checks
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.Constants
import org.cascadebot.cascadebot.DiscordPermissionException
import org.cascadebot.cascadebot.Environment
import org.cascadebot.cascadebot.MDCException
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.metrics.Metrics
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.PasteUtils
import org.cascadebot.cascadebot.utils.buttons.Button.UnicodeButton
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup
import org.cascadebot.cascadebot.utils.buttons.IButtonRunnable
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageCache
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit


object Messaging {

    @JvmStatic
    @JvmOverloads
    fun sendMessage(type: MessageType, channel: MessageChannel, message: String, embed: Boolean = true): CompletableFuture<Message> {
        Metrics.INS.messagesSent.labels(type.name).inc()
        return if (embed) {
            channel.sendMessage(MessagingObjects.getMessageTypeEmbedBuilder(type).setDescription(message).build()).submit()
        } else {
            channel.sendMessage(MessagingObjects.getMessageTypeMessageBuilder(type).append(message).build()).submit()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun sendEmbedMessage(type: MessageType, channel: MessageChannel, builder: EmbedBuilder, embed: Boolean = true): CompletableFuture<Message> {
        return if (embed) {
            channel.sendMessage(builder.setColor(type.color).build()).submit()
        } else {
            channel.sendMessage(type.emoji + " " + FormatUtils.formatEmbed(builder.build())).submit()
        }
    }

    @JvmStatic
    fun sendExceptionMessage(channel: MessageChannel, s: String, e: Throwable): CompletableFuture<Message> {
        val locale = if (channel is TextChannel) Language.getGuildLocale(channel.guild.idLong) else Locale.getDefaultLocale()
        var message = Language.i18n(locale, "messaging.exception_message", s, PasteUtils.paste(PasteUtils.getStackTrace(MDCException.from(e))))
        if (Environment.isProduction()) {
            message += """
                
                ${Language.i18n(locale, "messaging.report_error", Constants.serverInvite)}
                """.trimIndent()
        }
        return sendMessage(MessageType.DANGER, channel, message)
    }

    @JvmStatic
    fun sendAutoDeleteMessage(channel: MessageChannel, message: String, delay: Long) {
        channel.sendMessage(message).queue {
            // We should always be able to delete our own message
            it.delete().queueAfter(delay, TimeUnit.MILLISECONDS)
        }
    }

    @JvmStatic
    fun sendAutoDeleteMessage(channel: MessageChannel, embed: MessageEmbed, delay: Long) {
        channel.sendMessage(embed).queue {
            // We should always be able to delete our own message
            it.delete().queueAfter(delay, TimeUnit.MILLISECONDS)
        }
    }

    @JvmStatic
    fun sendAutoDeleteMessage(channel: MessageChannel, message: Message, delay: Long) {
        channel.sendMessage(message).queue {
            // We should always be able to delete our own message
            it.delete().queueAfter(delay, TimeUnit.MILLISECONDS)
        }
    }

    @JvmStatic
    fun sendButtonedMessage(channel: TextChannel, message: Message, buttonGroup: ButtonGroup): CompletableFuture<Message> {
        if (!channel.guild.getMember(CascadeBot.INS.selfUser)!!.hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
            throw DiscordPermissionException(Permission.MESSAGE_ADD_REACTION)
        }
        val future = channel.sendMessage(message).submit()
        future.thenAccept {
            buttonGroup.addButtonsToMessage(it)
            GuildDataManager.getGuildData(it.guild.idLong).addButtonGroup(channel, it, buttonGroup)
        }
        return future
    }

    @JvmStatic
    fun sendButtonedMessage(channel: TextChannel, message: String, buttonGroup: ButtonGroup): CompletableFuture<Message> {
        Checks.notBlank(message, "message")
        return sendButtonedMessage(channel, MessageBuilder().append(message).build(), buttonGroup)
    }

    @JvmStatic
    fun sendButtonedMessage(channel: TextChannel, embed: MessageEmbed, buttonGroup: ButtonGroup): CompletableFuture<Message> {
        return sendButtonedMessage(channel, MessageBuilder().setEmbed(embed).build(), buttonGroup)
    }

    private val firstPageButton = UnicodeButton(UnicodeConstants.REWIND, IButtonRunnable { _: Member?, textChannel: TextChannel, message: Message ->
        val pageGroup = GuildDataManager.getGuildData(textChannel.guild.idLong).pageCache[message.idLong]
        handlePage(message, 1, pageGroup!!)
    })

    private val prevPageButton = UnicodeButton(UnicodeConstants.BACKWARD_ARROW, IButtonRunnable { _: Member?, textChannel: TextChannel, message: Message ->
        val pageGroup = GuildDataManager.getGuildData(textChannel.guild.idLong).pageCache[message.idLong]
        val newPage = pageGroup!!.currentPage - 1
        handlePage(message, newPage, pageGroup)
    })

    private val nextPageButton = UnicodeButton(UnicodeConstants.FORWARD_ARROW, IButtonRunnable { _: Member?, textChannel: TextChannel, message: Message ->
        val pageGroup = GuildDataManager.getGuildData(textChannel.guild.idLong).pageCache[message.idLong]
        val newPage = pageGroup!!.currentPage + 1
        handlePage(message, newPage, pageGroup)
    })

    private val lastPageButton = UnicodeButton(UnicodeConstants.FAST_FORWARD, IButtonRunnable { _: Member?, textChannel: TextChannel, message: Message ->
        val pageGroup = GuildDataManager.getGuildData(textChannel.guild.idLong).pageCache[message.idLong]
        handlePage(message, pageGroup!!.pages, pageGroup)
    })

    private fun handlePage(message: Message, newPage: Int, pageGroup: PageCache.Pages) {
        if (newPage < 1 || newPage > pageGroup.pages) {
            return
        }
        pageGroup.getPage(newPage).pageShow(message, newPage, pageGroup.pages)
        pageGroup.currentPage = newPage
    }

    @JvmStatic
    fun sendPagedMessage(channel: TextChannel, owner: Member, pages: List<Page>): CompletableFuture<Message>? {
        require(pages.isNotEmpty()) { "The number of pages cannot be zero!" }
        if (pages.size == 1) {
            val future = channel.sendMessage(Language.i18n(channel.guild.idLong, "messaging.loading_page")).submit()
            future.thenAccept { pages[0].pageShow(it, 1, pages.size) }
            return future
        }
        val group = ButtonGroup(owner.idLong, channel.idLong, channel.guild.idLong)
        if (pages.size > 2) {
            group.addButton(firstPageButton)
        }
        group.addButton(prevPageButton)
        group.addButton(nextPageButton)
        if (pages.size > 2) {
            group.addButton(lastPageButton)
        }
        val future = channel.sendMessage(Language.i18n(channel.guild.idLong, "messaging.loading_page")).submit()
        future.thenAccept { sentMessage: Message ->
            pages[0].pageShow(sentMessage, 1, pages.size)
            group.addButtonsToMessage(sentMessage)
            group.setMessage(sentMessage.idLong)
            val guildData = GuildDataManager.getGuildData(channel.guild.idLong)
            guildData.addButtonGroup(channel, sentMessage, group)
            guildData.pageCache.put(sentMessage.idLong, pages)
        }
        return future
    }

}