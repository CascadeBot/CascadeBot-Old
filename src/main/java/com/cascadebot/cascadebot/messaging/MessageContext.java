/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.messaging;

import com.cascadebot.cascadebot.CascadeBot;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.Checks;

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

    /**
     * Sends a message that auto deletes it's self after 5 seconds.
     *
     * @param message The string message to send.
     */
    public void sendAutoDeleteMessage(String message) {
        sendAutoDeleteMessage(message, TimeUnit.SECONDS.toMillis(5));
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param message The string message to send.
     * @param delay   The amount of time to wait before it deletes it's self.
     */
    public void sendAutoDeleteMessage(String message, long delay) {
        sendAutoDeleteMessage(new MessageBuilder().setContent(message).build(), delay);
    }

    /**
     * Sends a message that auto deletes it's self after 5 seconds.
     *
     * @param embed The {@link MessageEmbed} object to send
     */
    public void sendAutoDeleteEmbedMessage(MessageEmbed embed) {
        sendAutoDeleteEmbedMessage(embed, TimeUnit.SECONDS.toMillis(5));
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param embed The {@link MessageEmbed} object to send
     * @param delay The amount of time to wait before it deletes it's self.
     */
    public void sendAutoDeleteEmbedMessage(MessageEmbed embed, long delay) {
        sendAutoDeleteMessage(new MessageBuilder().setEmbed(embed).build(), delay);
    }

    /**
     * Sends a message that auto deletes it's self after 5 seconds.
     *
     * @param message The {@link Message} object to send.
     */
    public void sendAutoDeleteMessage(Message message) {
        sendAutoDeleteMessage(message, TimeUnit.SECONDS.toMillis(5));
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param message The {@link Message} object to send.
     * @param delay   The amount of time to wait before it deletes it's self.
     */
    public void sendAutoDeleteMessage(Message message, long delay) {
        channel.sendMessage(message).queue(messageToDelete -> {
            if (canDeleteMessage(getSelfMember(), messageToDelete)) {
                messageToDelete.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
            }
        });
    }

    /**
     * Checks if a specific {@link Member} can delete the specified {@link Message}
     *
     * @param member  The {@link Member} used to check.
     * @param message The {@link Message} to check.
     * @return true if the {@link Member} can delete the {@link Message}, else false.
     */
    public boolean canDeleteMessage(Member member, Message message) {
        if(message.getChannel().getType().isGuild()) {
            TextChannel channel = message.getTextChannel();
            return member.hasPermission(channel, Permission.MESSAGE_MANAGE);
        } else {
            return member.getUser().getIdLong() == message.getAuthor().getIdLong();
        }
    }

    /**
     * Checks if a specific {@link Member} can delete messages in this {@link this#channel}.
     *
     * @param member The {@link Member} used to check.
     * @return true if the {@link Member} can delete message is this {@link this#channel}.
     */
    public boolean canDeleteMessages(Member member) {
        return canDeleteMessages(member, this.channel);
    }

    public boolean canDeleteMessages(Member member, TextChannel channel) {
        return member.hasPermission(channel, Permission.MESSAGE_MANAGE);
    }

    /**
     * Checks the permission for the member and channel provided for the context
     * Usually this is the channel a command was sent in and the member who send the command
     *
     * @param permissions Non-null and non empty permissions to check
     * @return true if the member has all of the specified permissions in the channel
     * @throws IllegalArgumentException if permissions are empty or null
     */
    public boolean hasPermission(Permission... permissions) {
        Checks.notEmpty(permissions, "Permissions");
        return this.member.hasPermission(this.channel, permissions);
    }

    /**
     * Checks the permissions for the specified member in the channel provided for this context
     *
     * @param member      the non-null member to check permissions for. The member needs to be in the same guild as the guild in the context
     * @param permissions permissions Non-null and non empty permissions to check
     * @return true if the member has all of the specified permissions in the channel
     * @throws IllegalArgumentException if member is null or not in the same guild
     * @throws IllegalArgumentException if permissions are empty or null
     */
    public boolean hasPermission(Member member, Permission... permissions) {
        Checks.notNull(member, "Member");
        Checks.check(member.getGuild().getIdLong() == guild.getIdLong(),
                "Member needs to be in the same guild as this context! Guild ID: " + guild.getId());
        Checks.notEmpty(permissions, "Permissions");
        return this.member.hasPermission(this.channel, permissions);
    }

    /**
     * Gets the bot {@link Member}.
     *
     * @return The bot {@link Member} for this guild
     */
    public Member getSelfMember() {
        return guild.getMember(CascadeBot.instance().getSelfUser());
    }

    /**
     * Sends a dm to the user in this context.
     *
     * @param message The message to send.
     */
    public void sendDm(String message) {
        sendDm(message, false);
    }

    /**
     * Sends a dm to the user in this context.
     *
     * @param message      The message to send.
     * @param allowChannel Weather or not we should send to a channel if dms are closed off.
     */
    public void sendDm(String message, boolean allowChannel) {
        sendDmMessage(new MessageBuilder().setContent(message).build(), allowChannel);
    }

    /**
     * Sends an {@link MessageEmbed} to the user in this context.
     *
     * @param embed The {@link MessageEmbed} object to send.
     */
    public void sendEmbedDm(MessageEmbed embed) {
        sendEmbedDm(embed, false);
    }

    /**
     * Sends an {@link MessageEmbed} to the user in this context.
     *
     * @param embed        The {@link MessageEmbed} object to send.
     * @param allowChannel Weather or not we should send to a channel if dms are closed off.
     */
    public void sendEmbedDm(MessageEmbed embed, boolean allowChannel) {
        sendDmMessage(new MessageBuilder().setEmbed(embed).build(), allowChannel);
    }

    /**
     * Sends a {@link Message} object to the user in this context.
     *
     * @param message The {@link Message} object to send.
     */
    public void sendDmMesage(Message message) {
        sendDmMessage(message, false);
    }

    /**
     * Sends an {@link MessageEmbed} to the user in this context.
     *
     * @param message      The {@link Message} object to send.
     * @param allowChannel Weather or not we should send to a channel if dms are closed off.
     */
    public void sendDmMessage(Message message, boolean allowChannel) {
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(), exception -> {
            if (allowChannel) {
                sendAutoDeleteMessage(message);
            }
        });
    }

}
