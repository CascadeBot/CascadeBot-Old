/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.messaging;

import com.cascadebot.cascadebot.tasks.AutoDeleteMessageTask;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public class MessageContext {

    private final TextChannel channel;
    private final Message message;
    private final Guild guild;
    private final Member member;

    public MessageContext(TextChannel channel, Message message, Guild guild, Member member) {
        this.channel = channel;
        this.message = message;
        this.guild = guild;
        this.member = member;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public Message getMessage() {
        return message;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getMember() {
        return member;
    }

    public User getUser() {
        return member.getUser();
    }

    public void sendAutoDeleteMessage(String message) {
        sendAutoDeleteMessage(message, TimeUnit.SECONDS.toMillis(5));
    }

    public void sendAutoDeleteMessage(String message, long delay) {
        channel.sendMessage(message).queue(deleteMessage -> new AutoDeleteMessageTask(deleteMessage, delay).run());
    }

    public void sendAutoDeleteEmbedMessage(MessageEmbed embed) {
        sendAutoDeleteEmbedMessage(embed, TimeUnit.SECONDS.toMillis(5));
    }

    public void sendAutoDeleteEmbedMessage(MessageEmbed embed, long delay) {
        channel.sendMessage(embed).queue(deleteMessage -> new AutoDeleteMessageTask(deleteMessage, delay).run());
    }

    public void sendDm(String message) {
       sendDm(message, false);
    }

    public void sendDm(String message, boolean allowChannel) {
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(), exception -> {
            if(allowChannel) {
                sendAutoDeleteMessage(message);
            }
        });
    }

    public void sendEmbedDm(MessageEmbed embed) {
        sendEmbedDm(embed, false);
    }

    public void sendEmbedDm(MessageEmbed embed, boolean allowChannel) {
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(embed).queue(), exception -> {
            if(allowChannel) {
                sendAutoDeleteEmbedMessage(embed);
            }
        });
    }
}
