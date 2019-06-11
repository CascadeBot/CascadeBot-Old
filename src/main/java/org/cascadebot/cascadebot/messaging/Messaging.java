/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RequestFuture;
import net.dv8tion.jda.core.utils.Checks;
import org.cascadebot.cascadebot.Cascade;
import org.cascadebot.cascadebot.Constants;
import org.cascadebot.cascadebot.Environment;
import org.cascadebot.cascadebot.MDCException;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.exceptions.DiscordPermissionException;
import org.cascadebot.cascadebot.metrics.Metrics;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.PasteUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageCache;

import java.util.List;
import java.util.concurrent.TimeUnit;

@UtilityClass
public final class Messaging {

    public static RequestFuture<Message> sendMessageTypeMessage(MessageChannel channel, MessageType type, String message, boolean embed) {
        Checks.notNull(channel, "channel");
        Metrics.INS.messagesSent.labels(type.name()).inc();
        if (embed) {
            return channel.sendMessage(MessagingObjects.getMessageTypeEmbedBuilder(type).setDescription(message).build()).submit();
        } else {
            return channel.sendMessage(MessagingObjects.getMessageTypeMessageBuilder(type).append(message).build()).submit();
        }
    }

    public static RequestFuture<Message> sendMessageTypeEmbedMessage(MessageChannel channel, MessageType type, EmbedBuilder builder, boolean embed) {
        Checks.notNull(channel, "channel");
        if (embed) {
            return channel.sendMessage(builder.setColor(type.getColor()).build()).submit();
        } else {
            return channel.sendMessage(type.getEmoji() + " " + FormatUtils.formatEmbed(builder.build())).submit();
        }
    }

    public static RequestFuture<Message> sendInfoMessage(MessageChannel channel, String message) {
        return sendInfoMessage(channel, message, true);
    }

    public static RequestFuture<Message> sendInfoMessage(MessageChannel channel, String message, boolean embed) {
        return sendMessageTypeMessage(channel, MessageType.INFO, message, embed);
    }

    public static RequestFuture<Message> sendInfoMessage(MessageChannel channel, EmbedBuilder builder) {
        return sendMessageTypeEmbedMessage(channel, MessageType.INFO, builder, true);
    }

    public static RequestFuture<Message> sendInfoMessage(MessageChannel channel, EmbedBuilder builder, boolean embed) {
        return sendMessageTypeEmbedMessage(channel, MessageType.INFO, builder, embed);
    }

    public static RequestFuture<Message> sendSuccessMessage(MessageChannel channel, String message) {
        return sendSuccessMessage(channel, message, true);
    }

    public static RequestFuture<Message> sendSuccessMessage(MessageChannel channel, String message, boolean embed) {
        return sendMessageTypeMessage(channel, MessageType.SUCCESS, message, embed);
    }

    public static RequestFuture<Message> sendSuccessMessage(MessageChannel channel, EmbedBuilder builder) {
        return sendMessageTypeEmbedMessage(channel, MessageType.SUCCESS, builder, true);
    }

    public static RequestFuture<Message> sendSuccessMessage(MessageChannel channel, EmbedBuilder builder, boolean embed) {
        return sendMessageTypeEmbedMessage(channel, MessageType.SUCCESS, builder, embed);
    }

    public static RequestFuture<Message> sendWarningMessage(MessageChannel channel, String message) {
        return sendWarningMessage(channel, message, true);
    }

    public static RequestFuture<Message> sendWarningMessage(MessageChannel channel, String message, boolean embed) {
        return sendMessageTypeMessage(channel, MessageType.WARNING, message, embed);
    }

    public static RequestFuture<Message> sendWarningMessage(MessageChannel channel, EmbedBuilder builder) {
        return sendMessageTypeEmbedMessage(channel, MessageType.WARNING, builder, true);
    }

    public static RequestFuture<Message> sendWarningMessage(MessageChannel channel, EmbedBuilder builder, boolean embed) {
        return sendMessageTypeEmbedMessage(channel, MessageType.WARNING, builder, embed);
    }

    public static RequestFuture<Message> sendModerationMessage(MessageChannel channel, String message) {
        return sendModerationMessage(channel, message, true);
    }

    public static RequestFuture<Message> sendModerationMessage(MessageChannel channel, String message, boolean embed) {
        return sendMessageTypeMessage(channel, MessageType.MODERATION, message, embed);
    }

    public static RequestFuture<Message> sendModerationMessage(MessageChannel channel, EmbedBuilder builder) {
        return sendMessageTypeEmbedMessage(channel, MessageType.MODERATION, builder, true);
    }

    public static RequestFuture<Message> sendModerationMessage(MessageChannel channel, EmbedBuilder builder, boolean embed) {
        return sendMessageTypeEmbedMessage(channel, MessageType.MODERATION, builder, embed);
    }

    public static RequestFuture<Message> sendDangerMessage(MessageChannel channel, String message) {
        return sendDangerMessage(channel, message, true);
    }

    public static RequestFuture<Message> sendDangerMessage(MessageChannel channel, String message, boolean embed) {
        return sendMessageTypeMessage(channel, MessageType.DANGER, message, embed);
    }

    public static RequestFuture<Message> sendDangerMessage(MessageChannel channel, EmbedBuilder builder) {
        return sendMessageTypeEmbedMessage(channel, MessageType.DANGER, builder, true);
    }

    public static RequestFuture<Message> sendDangerMessage(MessageChannel channel, EmbedBuilder builder, boolean embed) {
        return sendMessageTypeEmbedMessage(channel, MessageType.DANGER, builder, embed);
    }

    public static RequestFuture<Message> sendExceptionMessage(MessageChannel channel, String s, Throwable e) {
        String message = "**" + s + "**" +
                "\nStack trace: " + PasteUtils.paste(PasteUtils.getStackTrace(MDCException.from(e))) +
                (Environment.isDevelopment() ? "" : "\nPlease report the stack trace and the error to the developers here: " + Constants.serverInvite);
        return sendDangerMessage(channel, message);
    }

    public static void sendAutoDeleteMessage(MessageChannel channel, String message, long delay) {
        channel.sendMessage(message).queue(messageToDelete -> {
            // We should always be able to delete our own message
            messageToDelete.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
        });
    }

    public static void sendAutoDeleteMessage(MessageChannel channel, MessageEmbed embed, long delay) {
        channel.sendMessage(embed).queue(messageToDelete -> {
            // We should always be able to delete our own message
            messageToDelete.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
        });
    }

    public static void sendAutoDeleteMessage(MessageChannel channel, Message message, long delay) {
        channel.sendMessage(message).queue(messageToDelete -> {
            // We should always be able to delete our own message
            messageToDelete.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
        });
    }

    public static RequestFuture<Message> sendButtonedMessage(TextChannel channel, Message message, ButtonGroup buttonGroup) {
        Checks.notNull(message, "message");
        Checks.notNull(channel, "channel");
        Checks.notNull(buttonGroup, "button group");

        if (!channel.getGuild().getMember(Cascade.INS.getSelfUser()).hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
            throw new DiscordPermissionException(Permission.MESSAGE_ADD_REACTION);
        }

        RequestFuture<Message> future = channel.sendMessage(message).submit();
        future.thenAccept((sentMessage -> {
            buttonGroup.addButtonsToMessage(sentMessage);
            GuildDataManager.getGuildData(sentMessage.getGuild().getIdLong()).addButtonGroup(channel, sentMessage, buttonGroup);
        }));
        return future;
    }

    public static RequestFuture<Message> sendButtonedMessage(TextChannel channel, String message, ButtonGroup buttonGroup) {
        Checks.notBlank(message, "message");
        return sendButtonedMessage(channel, new MessageBuilder().append(message).build(), buttonGroup);
    }

    public static RequestFuture<Message> sendButtonedMessage(TextChannel channel, MessageEmbed embed, ButtonGroup buttonGroup) {
        Checks.notNull(embed, "embed");
        return sendButtonedMessage(channel, new MessageBuilder().setEmbed(embed).build(), buttonGroup);
    }

    public static RequestFuture<Message> sendPagedMessage(TextChannel channel, Member owner, List<Page> pages) {
        ButtonGroup group = new ButtonGroup(owner.getUser().getIdLong(), channel.getIdLong(), channel.getGuild().getIdLong());
        group.addButton(new Button.UnicodeButton(UnicodeConstants.REWIND, (runner, textChannel, message) -> {
            PageCache.Pages pageGroup = GuildDataManager.getGuildData(textChannel.getGuild().getIdLong()).getPageCache().get(message.getIdLong());
            pageGroup.getPage(1).pageShow(message, 1, pageGroup.getPages());
            pageGroup.setCurrentPage(1);
        }));
        group.addButton(new Button.UnicodeButton(UnicodeConstants.LEFT_ARROW, (runner, textChannel, message) -> {
            PageCache.Pages pageGroup = GuildDataManager.getGuildData(textChannel.getGuild().getIdLong()).getPageCache().get(message.getIdLong());
            int newPage = pageGroup.getCurrentPage() - 1;
            if (newPage < 1) {
                return;
            }
            pageGroup.getPage(newPage).pageShow(message, newPage, pageGroup.getPages());
            pageGroup.setCurrentPage(newPage);
        }));
        group.addButton(new Button.UnicodeButton(UnicodeConstants.RIGHT_ARROW, (runner, textChannel, message) -> {
            PageCache.Pages pageGroup = GuildDataManager.getGuildData(textChannel.getGuild().getIdLong()).getPageCache().get(message.getIdLong());
            int newPage = pageGroup.getCurrentPage() + 1;
            if (newPage > pageGroup.getPages()) {
                return;
            }
            pageGroup.getPage(newPage).pageShow(message, newPage, pageGroup.getPages());
            pageGroup.setCurrentPage(newPage);
        }));
        group.addButton(new Button.UnicodeButton(UnicodeConstants.FAST_FORWARD, (runner, textChannel, message) -> {
            PageCache.Pages pageGroup = GuildDataManager.getGuildData(textChannel.getGuild().getIdLong()).getPageCache().get(message.getIdLong());
            pageGroup.getPage(pageGroup.getPages()).pageShow(message, pageGroup.getPages(), pageGroup.getPages());
            pageGroup.setCurrentPage(pageGroup.getPages());
        }));
        RequestFuture<Message> future = channel.sendMessage("Paged message loading...").submit();
        future.thenAccept(sentMessage -> {
            pages.get(0).pageShow(sentMessage, 1, pages.size());
            group.addButtonsToMessage(sentMessage);
            group.setMessage(sentMessage.getIdLong());
            GuildData guildData = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
            guildData.addButtonGroup(channel, sentMessage, group);
            guildData.getPageCache().put(pages, sentMessage.getIdLong());
        });
        return future;
    }


}
