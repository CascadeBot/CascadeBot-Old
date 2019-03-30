/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.GuildSettings;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingDirectMessage;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.messaging.MessagingTimed;
import org.cascadebot.cascadebot.messaging.MessagingTyped;
import org.cascadebot.cascadebot.messaging.MessagingUI;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.shared.Regex;
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
import net.dv8tion.jda.core.utils.Checks;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
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

    private final MessagingTyped messagingTyped = new MessagingTyped(this);
    private final MessagingDirectMessage messagingDirectMessage = new MessagingDirectMessage(this);
    private final MessagingUI messagingUI = new MessagingUI(this);
    private final MessagingTimed messagingTimed = new MessagingTimed(this);

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

    public GuildSettings getSettings() {
        return data.getSettings();
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

    //region Messaging Objects

    public MessagingTyped getTypedMessaging() {
        return messagingTyped;
    }

    public MessagingDirectMessage getDirectMessageMessaging() {
        return messagingDirectMessage;
    }

    public MessagingUI getUIMessaging() {
        return messagingUI;
    }

    public MessagingTimed getTimedMessaging() {
        return messagingTimed;
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

    public String getUsage(ICommandExecutable command) {
        return getUsage(command, null);
    }

    public String getUsage(ICommandExecutable command, String parent) {
        Set<Argument> arguments = new HashSet<>(command.getUndefinedArguments());
        if (command instanceof ICommandMain) {
            for (ICommandExecutable subCommand : ((ICommandMain) command).getSubCommands()) {
                arguments.add(Argument.of(subCommand.command(), subCommand.description(), subCommand.getUndefinedArguments()));
            }
        }

        Argument parentArg = Argument.of(command.command(), command.description(), arguments);

        int levels = 0;
        for (String arg : args) {
            levels++;
            Argument argument = getArgFromSet(parentArg.getSubArgs(), arg);
            if (argument != null) {
                parentArg = argument;
            }
        }

        String commandString = data.getPrefix() + (parent == null ? "" : parent + " ") + (levels > 0 ? command.command() + " " + (levels > 1 ? getMessage(0, levels - 1) + " " : "") : "");
        return parentArg.getUsageString(commandString);
    }

    private Argument getArgFromSet(Set<Argument> arguments, String arg) {
        for (Argument argument : arguments) {
            if (argument.argStartsWith(arg)) {
                return argument;
            }
        }
        return null;
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
        return guild.getMember(getSelfUser());
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
