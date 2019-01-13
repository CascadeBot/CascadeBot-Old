/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.messaging;

import com.cascadebot.cascadebot.utils.FormatUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.requests.RequestFuture;
import net.dv8tion.jda.core.utils.Checks;

import java.util.concurrent.TimeUnit;

public final class Messaging {

    public static RequestFuture<Message> sendMessageTypeMessage(MessageChannel channel, MessageType type, String message, boolean embed) {
        Checks.notNull(channel, "channel");
        if (embed) {
            return channel.sendMessage(MessagingObjects.getMessageTypeEmbedBuilder(type).setDescription(message).build()).submit();
        } else {
            return channel.sendMessage(MessagingObjects.getMessageTypeMessageBuilder(type).append(message).build()).submit();
        }
    }

    public static RequestFuture<Message> sendMessageTypeEmbedMessage(MessageChannel channel, MessageType type, EmbedBuilder builder, boolean embed) {
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


}
