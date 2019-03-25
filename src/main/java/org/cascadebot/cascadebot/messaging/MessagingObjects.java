/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;

public final class MessagingObjects {

    private static ThreadLocal<MessageBuilder> threadLocalMessageBuilder = ThreadLocal.withInitial(MessageBuilder::new);
    private static ThreadLocal<EmbedBuilder> threadLocalEmbedBuilder = ThreadLocal.withInitial(EmbedBuilder::new);

    public static MessageBuilder getClearThreadLocalMessageBuilder() {
        return threadLocalMessageBuilder.get().clear();
    }

    public static EmbedBuilder getClearThreadLocalEmbedBuilder() {
        return threadLocalEmbedBuilder.get()
                .clearFields()
                .setTitle(null)
                .setDescription(null)
                .setTimestamp(null)
                .setColor(null)
                .setThumbnail(null)
                .setAuthor(null, null, null)
                .setFooter(null, null)
                .setImage(null);
    }

    public static EmbedBuilder getStandardMessageEmbed(String message, User requestedBy) {
        return getClearThreadLocalEmbedBuilder()
                .setDescription(message)
                .setFooter("Requested by " + requestedBy.getAsTag(), requestedBy.getEffectiveAvatarUrl());
    }

    public static EmbedBuilder getMessageTypeEmbedBuilder(MessageType messageType) {
        return getClearThreadLocalEmbedBuilder().setColor(messageType.getColor());
    }

    public static MessageBuilder getMessageTypeMessageBuilder(MessageType messageType) {
        return getClearThreadLocalMessageBuilder().append(messageType.getEmoji()).append(" ");
    }


}
