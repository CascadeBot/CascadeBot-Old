/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.data.Config;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.messaging.Messaging;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.cascadebot.cascadebot.utils.pagination.Page;
import com.cascadebot.shared.Regex;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import net.dv8tion.jda.core.utils.Checks;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandContext {


    private final GuildData data;

    private final JDA jda;
    private final TextChannel channel;
    private final Message message;
    private final Guild guild;
    private final Member member;

    private final String[] args;
    private final String trigger;
    private final boolean isMention;

    public CommandContext(JDA jda, TextChannel channel, Message message, Guild guild, GuildData data, String[] args, Member invoker,
                          String trigger, boolean isMention) {
        this.jda = jda;
        this.channel = channel;
        this.message = message;
        this.guild = guild;
        this.member = invoker;
        this.data = data;
        this.args = args;
        this.trigger = trigger;
        this.isMention = isMention;
    }


    //region Raw data getters

    public GuildData getData() {
        return data;
    }

    public JDA getJDA() {
        return jda;
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

    public String[] getArgs() {
        return args;
    }

    public String getTrigger() {
        return trigger;
    }

    public boolean isMention() {
        return isMention;
    }

    //endregion

    //region Helper methods for arguments

    public String getMessage(int start) {
        return getMessage(start, args.length);
    }

    public String getMessage(int start, int end) {
        return String.join(" ", ArrayUtils.subarray(args, start, end));
    }

    public boolean isArgInteger(int index) {
        return Regex.INTEGER_REGEX.matcher(this.args[index]).matches();
    }

    public boolean isArgDecimal(int index) {
        return Regex.DECIMAL_REGEX.matcher(this.args[index]).matches();
    }

    public String getArg(int index) {
        return this.args[index];
    }

    public int getArgAsInteger(int index) {
        return Integer.parseInt(this.args[index]);
    }

    public Double getArgAsDouble(int index) {
        return Double.parseDouble(StringUtils.replace(this.args[index], ",", "."));
    }

    //endregion

    //region Message Methods

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

    public void reply(MessageEmbed embed) {
        Checks.notNull(embed, "embed");
        channel.sendMessage(embed).queue();
    }

    public void reply(Message message) {
        Checks.notNull(message, "message");
        channel.sendMessage(message).queue();
    }

    public void replyInfo(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendInfoMessage(channel, MessagingObjects.getStandardMessageEmbed(message, getUser()), data.getUseEmbedForMessages());
    }

    public void replyInfo(String message, Object... objects) {
        replyInfo(String.format(message, objects));
    }

    public void replyInfo(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendInfoMessage(channel, builder, data.getUseEmbedForMessages());
    }

    public void replySuccess(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendSuccessMessage(channel, MessagingObjects.getStandardMessageEmbed(message, getUser()), data.getUseEmbedForMessages());
    }

    public void replySuccess(String message, Object... objects) {
        replySuccess(String.format(message, objects));
    }

    public void replySuccess(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendSuccessMessage(channel, builder, data.getUseEmbedForMessages());
    }

    public void replyWarning(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendWarningMessage(channel, MessagingObjects.getStandardMessageEmbed(message, getUser()), data.getUseEmbedForMessages());
    }

    public void replyWarning(String message, Object... objects) {
        replyWarning(String.format(message, objects));
    }

    public void replyWarning(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendWarningMessage(channel, builder, data.getUseEmbedForMessages());
    }

    public void replyModeration(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendModerationMessage(channel, MessagingObjects.getStandardMessageEmbed(message, getUser()), data.getUseEmbedForMessages());
    }

    public void replyModeration(String message, Object... objects) {
        replyModeration(String.format(message, objects));
    }

    public void replyModeration(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendModerationMessage(channel, builder, data.getUseEmbedForMessages());
    }

    public void replyDanger(String message) {
        Checks.notBlank(message, "message");
        Messaging.sendDangerMessage(channel, MessagingObjects.getStandardMessageEmbed(message, getUser()), data.getUseEmbedForMessages());
    }

    public void replyDanger(String message, Object... objects) {
        replyDanger(String.format(message, objects));
    }

    public void replyDanger(EmbedBuilder builder) {
        Checks.notNull(builder, "build");
        Messaging.sendDangerMessage(channel, builder, data.getUseEmbedForMessages());
    }

    public void replyException(String message, Throwable throwable) {
        Messaging.sendExceptionMessage(channel, message, new CommandException(throwable, guild, trigger));
    }

    public void replyException(String message, Throwable throwable, Object... objects) {
        Messaging.sendExceptionMessage(channel, String.format(message, objects), new CommandException(throwable, guild, trigger));
    }

    public MessageAction replyImage(String url) {
        if (getData().getUseEmbedForMessages()) {
            EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
            embedBuilder.setImage(url);
            return channel.sendMessage(embedBuilder.build());
        } else {
            return channel.sendMessage(url);
        }
    }

    public void replyUsage(ICommandExecutable command) {
        replyUsage(command, null);
    }

    public void replyUsage(ICommandExecutable command, String parent) {
        Set<Argument> arguments = new HashSet<>(command.getUndefinedArguments());
        if(command instanceof ICommandMain) {
            for (ICommandExecutable subCommand : ((ICommandMain)command).getSubCommands()) {
                arguments.add(Argument.of(subCommand.command(), subCommand.description(), subCommand.getUndefinedArguments()));
            }
        }

        Argument parentArg = Argument.of(command.command(), command.description(), arguments);

        int levels = 0;
        for(String arg : args) {
            levels ++;
            Argument argument = getArgFromSet(parentArg.getSubArgs(), arg);
            if(argument != null) {
                parentArg = argument;
            }
        }

        String commandString = data.getCommandPrefix() + (parent == null ? "" : parent + " ") + (levels > 0 ? command.command() + " " + (levels > 1 ? getMessage(0, levels - 1) + " " : "") : "");
        replyWarning("Incorrect usage. Proper usage:\n" + parentArg.getUsageString(commandString));
    }

    private Argument getArgFromSet(Set<Argument> arguments, String arg) {
        for(Argument argument : arguments) {
            if(argument.argStartsWith(arg)) {
                return argument;
            }
        }
        return null;
    }

    public void sendPermissionsError(String permission) {
        replyDanger("You don't have the permission `%s` to do this!", permission);
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
        Messaging.sendAutoDeleteMessage(this.channel, message, delay);
    }

    /**
     * Sends a message that auto deletes it's self after the specified delay (in mills).
     *
     * @param embed The non-null {@link MessageEmbed} object to send.
     * @param delay The amount of time to wait before it deletes it's self.
     */
    public void sendAutoDeleteMessage(MessageEmbed embed, long delay) {
        Checks.notNull(embed, "embed");
        Messaging.sendAutoDeleteMessage(this.channel, embed, delay);
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
        Messaging.sendAutoDeleteMessage(this.channel, message, delay);
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
     * Get's the bot's {@link SelfUser}
     *
     * @return The bot's {@link SelfUser}
     */
    public SelfUser getSelfUser() {
        return jda.getSelfUser();
    }

    /**
     * Gets the bot's {@link Member}.
     *
     * @return The bot's {@link Member} for this guild.
     */
    public Member getSelfMember() {
        return guild.getMember(jda.getSelfUser());
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
                sendAutoDeleteMessage(message, 5000);
            }
        });
    }

    /**
     * Replies to the user in the context with a {@link MessageEmbed} by direct messages.
     *
     * @param embed The non-null {@link MessageEmbed} object to send.
     * @throws IllegalArgumentException if embed is null.
     * @see CommandContext#replyDM(MessageEmbed, boolean)
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
                sendAutoDeleteMessage(embed, 5000);
            }
        });
    }

    /**
     * Replies to the user in the context with a {@link Message} by direct messages.
     *
     * @param message The {@link Message} object to send.
     * @throws IllegalArgumentException if message is null.
     * @see CommandContext#replyDM(Message, boolean).
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
                sendAutoDeleteMessage(message, 5000);
            }
        });
    }

    public void sendButtonedMessage(String message, ButtonGroup group) {
        Messaging.sendButtonedMessage(channel, message, group);
    }

    public void sendButtonedMessage(MessageEmbed embed, ButtonGroup group) {
        Messaging.sendButtonedMessage(channel, embed, group);
    }

    public void sendButtonedMessage(Message message, ButtonGroup group) {
        Messaging.sendButtonedMessage(channel, message, group);
    }

    public void sendPagedMessage(List<Page> pages) {
        Messaging.sendPagedMessage(channel, member, pages);
    }

    //endregion

    //region Helper Methods

    public Emote getGlobalEmote(String key) {
        Long emoteId = Config.INS.getGlobalEmotes().get(key);
        if (emoteId != null) {
            return CascadeBot.INS.getShardManager().getEmoteById(emoteId);
        }
        CascadeBot.LOGGER.warn("Tried to get global emote that doesn't exist! Key: {}", key);
        return null;
    }

    public String globalEmote(String key) {
        Emote emote = getGlobalEmote(key);
        return emote == null ? "" : emote.getAsMention();
    }

    public boolean hasPermission(String permission) {
        CascadePermission cascadePermission = CascadeBot.INS.getPermissionsManager().getPermission(permission);
        return cascadePermission != null; // TODO: Check actual perms
    }

    //endregion


}
