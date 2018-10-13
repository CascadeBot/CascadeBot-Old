/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.messaging;

import com.cascadebot.cascadebot.CascadeBot;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.Checks;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageContext {

    private final TextChannel channel;
    private final Message message;
    private final Guild guild;
    private final Member member;

    private static final Pattern idPattern = Pattern.compile("[0-9]{18}");
    private static final Pattern mentionPattern = Pattern.compile("<@([0-9]{18})>");

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
     * Replies to the user in this context
     *
     * @param message the message to reply with which cannot be blank
     * @throws IllegalArgumentException if message is blank
     */
    public void reply(String message) {
        Checks.notBlank(message, "message");
        channel.sendMessage(message).queue();
    }

    public void reply(Message message) {
        Checks.notNull(message, "message");
        channel.sendMessage(message).queue();
    }

    /**
     * Sends a message that auto deletes it's self after 5 seconds.
     *
     * @param message The string message to send which cannot be blank.
     * @throws IllegalArgumentException if message is blank.
     */
    public void sendAutoDeleteMessage(String message) {
        sendAutoDeleteMessage(message, TimeUnit.SECONDS.toMillis(5));
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param message The string message to send which cannot be blank.
     * @param delay   The amount of time to wait before it deletes it's self.
     * @throws IllegalArgumentException if message is blank.
     */
    public void sendAutoDeleteMessage(String message, long delay) {
        Checks.notBlank(message, "message");
        channel.sendMessage(message).queue(messageToDelete -> {
            if (canDeleteMessage(getSelfMember(), messageToDelete)) {
                messageToDelete.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
            }
        });
    }

    /**
     * Sends a message that auto deletes it's self after 5 seconds.
     *
     * @param embed The {@link MessageEmbed} object to send
     */
    public void sendAutoDeleteMessage(MessageEmbed embed) {
        sendAutoDeleteMessage(embed, TimeUnit.SECONDS.toMillis(5));
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param embed The non-null {@link MessageEmbed} object to send.
     * @param delay The amount of time to wait before it deletes it's self.
     */
    public void sendAutoDeleteMessage(MessageEmbed embed, long delay) {
        Checks.notNull(embed, "embed");
        channel.sendMessage(message).queue(messageToDelete -> {
            if (canDeleteMessage(getSelfMember(), messageToDelete)) {
                messageToDelete.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
            }
        });
    }

    /**
     * Sends a message that auto deletes it's self after 5 seconds.
     *
     * @param message The non-null {@link Message} object to send.
     * @throws IllegalArgumentException if message is null.
     */
    public void sendAutoDeleteMessage(Message message) {
        Checks.notNull(message, "message");
        sendAutoDeleteMessage(message, TimeUnit.SECONDS.toMillis(5));
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param message The non-null {@link Message} object to send.
     * @param delay   The amount of time to wait before it deletes it's self.
     * @throws IllegalArgumentException if message is null.
     */
    public void sendAutoDeleteMessage(Message message, long delay) {
        Checks.notNull(message, "message");
        channel.sendMessage(message).queue(messageToDelete -> {
            if (canDeleteMessage(getSelfMember(), messageToDelete)) {
                messageToDelete.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
            }
        });
    }

    /**
     * Checks if a specific {@link Member} can delete the specified {@link Message}
     *
     * @param member  The non-null {@link Member} used to check.
     * @param message The non-null {@link Message} to check.
     * @return true if the {@link Member} can delete the {@link Message}, else false.
     * @throws IllegalArgumentException if member or message are null.
     */
    public boolean canDeleteMessage(Member member, Message message) {
        Checks.notNull(member, "member");
        Checks.notNull(message, "message");
        if (message.getChannel().getType().isGuild()) {
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
     * Checks the permission for the member and channel provided for the context.
     * Usually this is the channel a command was sent in and the member who send the command.
     *
     * @param permissions Non-null and non empty permissions to check.
     * @return true if the member has all of the specified permissions in the channel.
     * @throws IllegalArgumentException if permissions are empty or null.
     */
    public boolean hasPermission(Permission... permissions) {
        Checks.notEmpty(permissions, "Permissions");
        return this.member.hasPermission(this.channel, permissions);
    }

    /**
     * Checks the permissions for the specified member in the channel provided for this context.
     *
     * @param member      the non-null member to check permissions for. The member needs to be in the same guild as the guild in the context.
     * @param permissions permissions Non-null and non empty permissions to check.
     * @return true if the member has all of the specified permissions in the channel.
     * @throws IllegalArgumentException if member is null or not in the same guild.
     * @throws IllegalArgumentException if permissions are empty or null.
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
     * @return The bot {@link Member} for this guild.
     */
    public Member getSelfMember() {
        return guild.getMember(CascadeBot.instance().getSelfUser());
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
     * @param allowChannel Whether or not we should send to a channel if DMs are closed off.
     * @throws IllegalArgumentException if message is blank.
     */
    public void replyDM(String message, boolean allowChannel) {
        Checks.notBlank(message, "message");
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(), exception -> {
            if (allowChannel) {
                sendAutoDeleteMessage(message);
            }
        });
    }

    /**
     * Replies to the user in the context with a {@link MessageEmbed} by direct messages.
     *
     * @param embed The non-null {@link MessageEmbed} object to send.
     * @throws IllegalArgumentException if embed is null.
     * @see MessageContext#replyDM(MessageEmbed, boolean)
     */
    public void replyDM(MessageEmbed embed) {
        replyDM(embed, false);
    }

    /**
     * Replies to the user in the context with a {@link MessageEmbed} by direct messages.
     *
     * @param embed        The non-null {@link MessageEmbed} object to send.
     * @param allowChannel Whether or not we should send to a channel if DMs are closed off.
     * @throws IllegalArgumentException if embed is null.
     */
    public void replyDM(MessageEmbed embed, boolean allowChannel) {
        Checks.notNull(embed, "embed");
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(embed).queue(), exception -> {
            if (allowChannel) {
                sendAutoDeleteMessage(embed);
            }
        });
    }

    /**
     * Replies to the user in the context with a {@link Message} by direct messages.
     *
     * @param message The {@link Message} object to send.
     * @throws IllegalArgumentException if message is null.
     * @see MessageContext#replyDM(Message, boolean).
     */
    public void replyDM(Message message) {
        replyDM(message, false);
    }

    /**
     * Replies to the user in the context with a {@link Message} by direct messages.
     *
     * @param message      The {@link Message} object to send.
     * @param allowChannel Whether or not we should send to a channel if DMs are closed off.
     * @throws IllegalArgumentException if message is null.
     */
    public void replyDM(Message message, boolean allowChannel) {
        Checks.notNull(message, "message");
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(), exception -> {
            if (allowChannel) {
                sendAutoDeleteMessage(message);
            }
        });
    }

    /**
     * Gets the username#discrim for the {@link User} in this context.
     *
     * @return The username#discrim for the user in this context.
     */
    public String getTag() {
        return getTag(getUser());
    }

    /**
     * Gets the username#discrim for the specified {@link User}.
     *
     * @param user The {@link User} to get tag from.
     * @return The username#discrim for the specified {@link User}.
     * @throws IllegalArgumentException if the {@link User} is null.
     */
    public String getTag(User user) {
        Checks.notNull(user, "user");
        return user.getName() + "#" + user.getDiscriminator();
    }

    /**
     * Attempts to find a member using a string input.
     * The string can be their id, a mention, or a name.
     *
     * @param search The string to find the {@link Member} with.
     * @return The {@link Member} found or null if no member was found with that name.
     * @throws IllegalArgumentException if name is null.
     */
    public Member getMember(String search) {
        Checks.notNull(search, "user");
        String id = null;
        if (idPattern.matcher(search).matches()) {
            id = search;
        }
        Matcher matcher = mentionPattern.matcher(search);
        if (matcher.matches()) {
            id = matcher.group(1);
        }

        if (id != null) {
            User user = getUserById(id);
            if (user != null) {
                return guild.getMember(user);
            }
        }

        List<Member> members = guild.getMembersByEffectiveName(search, true);
        if (members.size() == 0) {
            return null;
        } else if (members.size() == 1) {
            return members.get(0);
        } else {
            //TODO maybe add an option to get users from a group with buttons?
            return null;
        }
    }

    public User getUserById(String id) {
        return CascadeBot.instance().getClient().getUserById(id);
    }
}
