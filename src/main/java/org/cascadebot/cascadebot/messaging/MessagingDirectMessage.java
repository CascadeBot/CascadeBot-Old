/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.utils.Checks;
import org.cascadebot.cascadebot.commandmeta.CommandContext;

public class MessagingDirectMessage { //TODO come up with better name

    private CommandContext context;

    public MessagingDirectMessage(CommandContext context) {
        this.context = context;
    }

    /**
     * Sends a DM to the user in this context.
     *
     * @param message The message to send.
     */
    public void replyDM(String message) {
        replyDM(message, false);
    }

    /**
     * Sends a DM to the user in this context.
     *
     * @param message      The message to send which cannot be blank.
     * @param allowChannel Whether or not we should send to the original channel if DMs are closed off.
     * @throws IllegalArgumentException if message is blank.
     */
    public void replyDM(String message, boolean allowChannel) {
        Checks.notBlank(message, "message");
        context.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(), exception -> {
            if (allowChannel) {
                context.getTimedMessaging().sendAutoDeleteMessage(message, 5000);
            }
        });
    }

    /**
     * Replies to the user in the context with a {@link MessageEmbed} by direct messages.
     *
     * @param embed The non-null {@link MessageEmbed} object to send.
     * @throws IllegalArgumentException if embed is null.
     * @see MessagingDirectMessage#replyDM(MessageEmbed, boolean)
     */
    public void replyDM(MessageEmbed embed) {
        replyDM(embed, false);
    }

    /**
     * Replies to the user in the context with a {@link MessageEmbed} by direct messages.
     *
     * @param embed        The non-null {@link MessageEmbed} object to send.
     * @param allowChannel Whether or not we should send to the original channel if DMs are closed off.
     * @throws IllegalArgumentException if embed is null.
     */
    public void replyDM(MessageEmbed embed, boolean allowChannel) {
        Checks.notNull(embed, "embed");
        context.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessage(embed).queue(), exception -> {
            if (allowChannel) {
                context.getTimedMessaging().sendAutoDeleteMessage(embed, 5000);
            }
        });
    }

    /**
     * Replies to the user in the context with a {@link Message} by direct messages.
     *
     * @param message The {@link Message} object to send.
     * @throws IllegalArgumentException if message is null.
     * @see MessagingDirectMessage#replyDM(Message, boolean).
     */
    public void replyDM(Message message) {
        replyDM(message, false);
    }

    /**
     * Replies to the user in the context with a {@link Message} by direct messages.
     *
     * @param message      The {@link Message} object to send.
     * @param allowChannel Whether or not we should send to the original channel if DMs are closed off.
     * @throws IllegalArgumentException if message is null.
     */
    public void replyDM(Message message, boolean allowChannel) {
        Checks.notNull(message, "message");
        context.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(), exception -> {
            if (allowChannel) {
                context.getTimedMessaging().sendAutoDeleteMessage(message, 5000);
            }
        });
    }

}
