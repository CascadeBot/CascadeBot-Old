/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.messaging;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;

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

    public static EmbedBuilder getMessageTypeEmbedBuilder(MessageType messageType) {
        return getClearThreadLocalEmbedBuilder().setColor(messageType.getColor());
    }

    public static EmbedBuilder getColoredEmbedBuilder() {
        return getMessageTypeEmbedBuilder(MessageType.NEUTRAL);
    }

    public static EmbedBuilder getSuccessEmbedBuilder() {
        return getMessageTypeEmbedBuilder(MessageType.SUCCESS);
    }

    public static EmbedBuilder getInfoEmbedBuilder() {
        return getMessageTypeEmbedBuilder(MessageType.INFO);
    }

    public static EmbedBuilder getWarningEmbedBuilder() {
        return getMessageTypeEmbedBuilder(MessageType.WARNING);
    }

    public static EmbedBuilder getDangerEmbedBuilder() {
        return getMessageTypeEmbedBuilder(MessageType.DANGER);
    }

    public static EmbedBuilder getModerationEmbedBuilder() {
        return getMessageTypeEmbedBuilder(MessageType.MODERATION);
    }

    public static MessageBuilder getMessageTypeMessageBuilder(MessageType messageType) {
        return getClearThreadLocalMessageBuilder().append(messageType.getEmoji()).append(" ");
    }

    public static MessageBuilder getColoredMessageBuilder() {
        return getMessageTypeMessageBuilder(MessageType.NEUTRAL);
    }

    public static MessageBuilder getSuccessMessageBuilder() {
        return getMessageTypeMessageBuilder(MessageType.SUCCESS);
    }

    public static MessageBuilder getInfoMessageBuilder() {
        return getMessageTypeMessageBuilder(MessageType.INFO);
    }

    public static MessageBuilder getWarningMessageBuilder() {
        return getMessageTypeMessageBuilder(MessageType.WARNING);
    }

    public static MessageBuilder getDangerMessageBuilder() {
        return getMessageTypeMessageBuilder(MessageType.DANGER);
    }

    public static MessageBuilder getModerationMessageBuilder() {
        return getMessageTypeMessageBuilder(MessageType.MODERATION);
    }


}
