/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.utils.Checks;
import org.cascadebot.cascadebot.commandmeta.CommandContext;

public class MessagingTyped {

    private CommandContext context;

    public MessagingTyped(CommandContext context) {
        this.context = context;
    }

    /**
     * Replies to the channel in the command context as an info message.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#INFO},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#INFO} at the top of the message
     *
     * @param message The String message to send.
     */
    public void replyInfo(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendInfoMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as an info message. After formatting the string using {@link String#format(String, Object...)}
     * When embeds for the guild are on, it replies with an embed with embed bar color being the color defined in {@link MessageType#INFO},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#INFO} at the top of the message
     *
     * @param message The String message to send.
     * @param objects The objects to use with the formatting.
     * @deprecated This should only be used temporarily. In production, language formatting should be used
     */
    @Deprecated
    public void replyInfo(String message, Object... objects) {
        replyInfo(String.format(message, objects));
    }

    /**
     * Replies to the channel in the command context as an info message using the passed in {@link EmbedBuilder}.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#INFO}
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#INFO} in the title and the embed formatted using {@link org.cascadebot.cascadebot.utils.FormatUtils#formatEmbed(MessageEmbed)}
     *
     * @param builder The embed builder to use as the message.
     */
    public void replyInfo(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendInfoMessage(context.getChannel(), builder, context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as a warning message.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#WARNING},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#WARNING} at the top of the message
     *
     * @param message The String message to send.
     */
    public void replyWarning(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendWarningMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as a warning message. After formatting the string using {@link String#format(String, Object...)}
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#INFO},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#INFO} at the top of the message
     *
     * @param message The String message to send.
     * @param objects The objects to use with the formatting.
     * @deprecated This should only be used temporarily. In production, language formatting should be used
     */
    @Deprecated
    public void replyWarning(String message, Object... objects) {
        replyWarning(String.format(message, objects));
    }

    /**
     * Replies to the channel in the command context as a warning message using the passed in {@link EmbedBuilder}.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#INFO}
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#INFO} in the title and the embed formatted using {@link org.cascadebot.cascadebot.utils.FormatUtils#formatEmbed(MessageEmbed)}
     *
     * @param builder The embed builder to use as the message.
     */
    public void replyWarning(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendWarningMessage(context.getChannel(), builder, context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as a success message.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#WARNING},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#WARNING} at the top of the message
     *
     * @param message The String message to send.
     */
    public void replySuccess(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendSuccessMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as a success message. After formatting the string using {@link String#format(String, Object...)}
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#WARNING},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#WARNING} at the top of the message
     *
     * @param message The String message to send.
     * @param objects The objects to use with the formatting.
     */
    public void replySuccess(String message, Object... objects) {
        replySuccess(String.format(message, objects));
    }

    /**
     * Replies to the channel in the command context as a success message using the passed in {@link EmbedBuilder}.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#WARNING}
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#WARNING} in the title and the embed formatted using {@link org.cascadebot.cascadebot.utils.FormatUtils#formatEmbed(MessageEmbed)}
     *
     * @param builder The embed builder to use as the message.
     */
    public void replySuccess(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendSuccessMessage(context.getChannel(), builder, context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as a moderation message.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#MODERATION},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#MODERATION} at the top of the message
     *
     * @param message The String message to send.
     */
    public void replyModeration(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendModerationMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as a moderation message. After formatting the string using {@link String#format(String, Object...)}
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#MODERATION},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#MODERATION} at the top of the message
     *
     * @param message The String message to send.
     * @param objects The objects to use with the formatting.
     * @deprecated This should only be used temporarily. In production, language formatting should be used
     */
    @Deprecated
    public void replyModeration(String message, Object... objects) {
        replyModeration(String.format(message, objects));
    }

    /**
     * Replies to the channel in the command context as a moderation message using the passed in {@link EmbedBuilder}.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#MODERATION}
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#MODERATION} in the title and the embed formatted using {@link org.cascadebot.cascadebot.utils.FormatUtils#formatEmbed(MessageEmbed)}
     *
     * @param builder The embed builder to use as the message.
     */
    public void replyModeration(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendModerationMessage(context.getChannel(), builder, context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as a danger message.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#DANGER},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#DANGER} at the top of the message
     *
     * @param message The String message to send.
     */
    public void replyDanger(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendDangerMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as a danger message. After formatting the string using {@link String#format(String, Object...)}
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#DANGER},
     * and with the message being in the description of the embed
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#DANGER} at the top of the message
     *
     * @param message The String message to send.
     * @param objects The objects to use with the formatting.
     * @deprecated This should only be used temporarily. In production, language formatting should be used
     */
    @Deprecated
    public void replyDanger(String message, Object... objects) {
        replyDanger(String.format(message, objects));
    }

    /**
     * Replies to the channel in the command context as a danger message using the passed in {@link EmbedBuilder}.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#DANGER}
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#DANGER} in the title and the embed formatted using {@link org.cascadebot.cascadebot.utils.FormatUtils#formatEmbed(MessageEmbed)}
     *
     * @param builder The embed builder to use as the message.
     */
    public void replyDanger(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendDangerMessage(context.getChannel(), builder, context.getCoreSettings().isUseEmbedForMessages());
    }

    /**
     * Replies to the channel in the command context as a danger message.
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#DANGER},
     * and with the message being in the description of the embed, and the stack trance as a link below it.
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#DANGER} at the top of the message, and the stack trance as a link below it.
     *
     * @param message   The String message to send.
     * @param throwable The Exception that your sending.
     */
    public void replyException(String message, Throwable throwable) {
        Messaging.sendExceptionMessage(context.getChannel(), message, throwable);
    }

    /**
     * Replies to the channel in the command context as a danger message. After formatting the string using {@link String#format(String, Object...)}
     * When embeds for the guild are on, it replies with an embed with the embed color being the color defined in {@link MessageType#DANGER},
     * and with the message being in the description of the embed, and the stack trance as a hastebin link below it.
     * If embeds are turned off, it replies with a normal message with the Unicode character defined in {@link MessageType#DANGER} at the top of the message, and the stack trance as a hastebin link below it.
     *
     * @param message   The String message to send.
     * @param throwable The Exception that your sending
     * @param objects   The objects to use with the formatting.
     * @deprecated This should only be used temporarily. In production, language formatting should be used
     */
    @Deprecated
    public void replyException(String message, Throwable throwable, Object... objects) {
        Messaging.sendExceptionMessage(context.getChannel(), String.format(message, objects), throwable);
    }

}
