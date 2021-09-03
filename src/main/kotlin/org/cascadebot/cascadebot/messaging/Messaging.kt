package org.cascadebot.cascadebot.messaging

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.interactions.components.ButtonStyle
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
import org.cascadebot.cascadebot.utils.interactions.IButtonRunnable
import org.cascadebot.cascadebot.utils.interactions.ISelectionRunnable
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow
import org.cascadebot.cascadebot.utils.interactions.CascadeButton
import org.cascadebot.cascadebot.utils.interactions.CascadeSelectBox
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer
import org.cascadebot.cascadebot.utils.interactions.InteractionMessage
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
            channel.sendMessage(MessagingObjects.getMessageTypeMessageBuilder(type)
                    .append(message)
                    .denyMentions(Message.MentionType.EVERYONE, Message.MentionType.HERE, Message.MentionType.ROLE).build())
                    .submit()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun sendEmbedMessage(type: MessageType, channel: MessageChannel, builder: EmbedBuilder, embed: Boolean = true): CompletableFuture<Message> {
        return if (embed) {
            channel.sendMessage(builder.setColor(type.color).build()).submit()
        } else {
            channel.sendMessage(MessageBuilder()
                    .denyMentions(Message.MentionType.EVERYONE, Message.MentionType.HERE, Message.MentionType.ROLE)
                    .setContent(type.emoji + " " + FormatUtils.formatEmbed(builder.build()))
                    .build()).submit()
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
    fun sendComponentMessage(channel: TextChannel, message: String, container: ComponentContainer): CompletableFuture<Message> {
        return sendComponentMessage(channel, MessageBuilder().append(message).build(), container)
    }

    @JvmStatic
    fun sendComponentMessage(channel: TextChannel, embed: MessageEmbed, container: ComponentContainer): CompletableFuture<Message> {
        return sendComponentMessage(channel, MessageBuilder().setEmbeds(embed).build(), container)
    }

    @JvmStatic
    fun sendComponentMessage(channel: TextChannel, message: Message, container: ComponentContainer): CompletableFuture<Message> {
        val future = channel.sendMessage(message).setActionRows(container.getComponents().map { it.toDiscordActionRow() }).submit()
        future.thenAccept {
            GuildDataManager.getGuildData(it.guild.idLong).addComponents(channel, it, container)
        }

        return future
    }

    private val firstPageButton = CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.REWIND),
        IButtonRunnable { _, textChannel, message ->
            val pageGroup = GuildDataManager.getGuildData(textChannel.guild.idLong).pageCache[message.idLong]
            handlePage(message, 1, pageGroup!!)
        })

    private val prevPageButton = CascadeButton.secondary("Prev Page", Emoji.fromUnicode(UnicodeConstants.BACKWARD_ARROW),
        IButtonRunnable { _, textChannel, message ->
            val pageGroup = GuildDataManager.getGuildData(textChannel.guild.idLong).pageCache[message.idLong]
            val newPage = pageGroup!!.currentPage - 1
            handlePage(message, newPage, pageGroup)
        })

    private val nextPageButton = CascadeButton.secondary("Next Page", Emoji.fromUnicode(UnicodeConstants.FORWARD_ARROW),
        IButtonRunnable { _, textChannel, message ->
            val pageGroup = GuildDataManager.getGuildData(textChannel.guild.idLong).pageCache[message.idLong]
            val newPage = pageGroup!!.currentPage + 1
            handlePage(message, newPage, pageGroup)
        })

    private val lastPageButton = CascadeButton.secondary(Emoji.fromUnicode(UnicodeConstants.FAST_FORWARD),
        IButtonRunnable { _, textChannel, message ->
            val pageGroup = GuildDataManager.getGuildData(textChannel.guild.idLong).pageCache[message.idLong]
            handlePage(message, pageGroup!!.pageCount, pageGroup)
        })

    private fun handlePage(message: InteractionMessage, newPage: Int, pageGroup: PageCache.Pages) {
        if (newPage < 1 || newPage > pageGroup.pageCount) {
            return
        }

        firstPageButton.disabled = false
        prevPageButton.disabled = false
        nextPageButton.disabled = false
        lastPageButton.disabled = false

        if (newPage <= 1) {
            firstPageButton.disabled = true
            prevPageButton.disabled = true
        }
        if (newPage >= pageGroup.pageCount) {
            nextPageButton.disabled = true
            lastPageButton.disabled = true
        }

        if (pageGroup.selectBox != null) {
            pageGroup.selectBox.clearDefaults()
            pageGroup.selectBox.addDefault("Page $newPage")
        }

        pageGroup.getPage(newPage).pageShow(message, newPage, pageGroup.pageCount)
        pageGroup.currentPage = newPage
    }

    @JvmStatic
    fun sendPagedMessage(channel: TextChannel, owner: Member, pages: List<Page>): CompletableFuture<Message>? {
        require(pages.isNotEmpty()) { "The number of pages cannot be zero!" }
        if (pages.size == 1) {
            val future = channel.sendMessage(Language.i18n(channel.guild.idLong, "messaging.loading_page")).submit()
            future.thenAccept { pages[0].pageShow(InteractionMessage(it, ComponentContainer()), 1, pages.size) }
            return future
        }
        val actionRow = CascadeActionRow()
        if (pages.size > 2) {
            actionRow.addComponent(firstPageButton)
            firstPageButton.disabled = true
        }
        actionRow.addComponent(prevPageButton)
        prevPageButton.disabled = true
        actionRow.addComponent(nextPageButton)
        if (pages.size > 2) {
            actionRow.addComponent(lastPageButton)
        }
        val container = ComponentContainer()
        container.addRow(actionRow)
        var select: CascadeSelectBox? = null;
            if (pages.size > 2) {
            select = CascadeSelectBox("pages-select",
                ISelectionRunnable { member: Member, textChannel: TextChannel, message: InteractionMessage, selected: List<String> ->
                    val pageGroup = GuildDataManager.getGuildData(textChannel.guild.idLong).pageCache[message.idLong]
                    var page: Int = selected[0].split(" ")[1].toInt()
                    handlePage(message, page, pageGroup!!)
                })
            var i = 1;
            for (page in pages) {
                if (page.title != null) {
                    select.addOption("Page " + i++, page.title)
                } else {
                    select.addOption("Page " + i++)
                }
            }
            select.addDefault("Page 1")
            val selectRow = CascadeActionRow()
            selectRow.addComponent(select)
            container.addRow(selectRow)
        }
        val future = sendComponentMessage(channel, Language.i18n(channel.guild.idLong, "messaging.loading_page"), container)
        future.thenAccept { sentMessage: Message ->
            pages[0].pageShow(InteractionMessage(sentMessage, container), 1, pages.size)
            val guildData = GuildDataManager.getGuildData(channel.guild.idLong)
            guildData.pageCache.put(sentMessage.idLong, pages, select)
        }
        return future
    }

}