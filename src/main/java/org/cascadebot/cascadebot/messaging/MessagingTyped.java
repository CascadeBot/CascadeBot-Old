/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.CommandException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.utils.Checks;

public class MessagingTyped {
    CommandContext context;
    public MessagingTyped(CommandContext context) {
        this.context = context;
    }

    public void replyInfo(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendInfoMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getSettings().useEmbedForMessages());
    }

    public void replyInfo(String message, Object... objects) {
        replyInfo(String.format(message, objects));
    }

    public void replyInfo(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendInfoMessage(context.getChannel(), builder, context.getSettings().useEmbedForMessages());
    }

    public void replyWarning(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendWarningMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getSettings().useEmbedForMessages());
    }

    public void replyWarning(String message, Object... objects) {
        replyWarning(String.format(message, objects));
    }

    public void replyWarning(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendWarningMessage(context.getChannel(), builder, context.getSettings().useEmbedForMessages());
    }

    public void replySuccess(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendSuccessMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getSettings().useEmbedForMessages());
    }

    public void replySuccess(String message, Object... objects) {
        replySuccess(String.format(message, objects));
    }

    public void replySuccess(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendSuccessMessage(context.getChannel(), builder, context.getSettings().useEmbedForMessages());
    }

    public void replyModeration(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendModerationMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getSettings().useEmbedForMessages());
    }

    public void replyModeration(String message, Object... objects) {
        replyModeration(String.format(message, objects));
    }

    public void replyModeration(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendModerationMessage(context.getChannel(), builder, context.getSettings().useEmbedForMessages());
    }

    public void replyDanger(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendDangerMessage(context.getChannel(), MessagingObjects.getStandardMessageEmbed(message, context.getUser()), context.getSettings().useEmbedForMessages());
    }

    public void replyDanger(String message, Object... objects) {
        replyDanger(String.format(message, objects));
    }

    public void replyDanger(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendDangerMessage(context.getChannel(), builder, context.getSettings().useEmbedForMessages());
    }

    public void replyException(String message, Throwable throwable) {
        Messaging.sendExceptionMessage(context.getChannel(), message, new CommandException(throwable, context.getGuild(), context.getTrigger()));
    }

    public void replyException(String message, Throwable throwable, Object... objects) {
        Messaging.sendExceptionMessage(context.getChannel(), String.format(message, objects), new CommandException(throwable, context.getGuild(), context.getTrigger()));
    }
}
