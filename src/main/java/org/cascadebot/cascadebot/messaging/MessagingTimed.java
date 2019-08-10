/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.utils.Checks;
import org.cascadebot.cascadebot.commandmeta.CommandContext;

public class MessagingTimed {

    private CommandContext context;

    public MessagingTimed(CommandContext context) {
        this.context = context;
    }

    /**
     * Sends a message that auto deletes itself after the specified delay (in milliseconds).
     *
     * @param message The string message to send which cannot be blank.
     * @param delay   The amount of time to wait before it deletes itself.
     * @throws IllegalArgumentException if message is blank.
     */
    public void sendAutoDeleteMessage(String message, long delay) {
        Checks.notBlank(message, "message");
        Messaging.sendAutoDeleteMessage(context.getChannel(), message, delay);
    }

    /**
     * Sends a message that auto deletes itself after the specified delay (in milliseconds).
     *
     * @param embed The non-null {@link MessageEmbed} object to send.
     * @param delay The amount of time to wait before it deletes itself.
     */
    public void sendAutoDeleteMessage(MessageEmbed embed, long delay) {
        Checks.notNull(embed, "embed");
        Messaging.sendAutoDeleteMessage(context.getChannel(), embed, delay);
    }

    /**
     * Sends a message that auto deletes itself after the specified delay (in milliseconds).
     *
     * @param message The non-null {@link Message} object to send.
     * @param delay   The amount of time to wait before it deletes itself.
     * @throws IllegalArgumentException if message is null.
     */
    public void sendAutoDeleteMessage(Message message, long delay) {
        Checks.notNull(message, "message");
        Messaging.sendAutoDeleteMessage(context.getChannel(), message, delay);
    }

}
