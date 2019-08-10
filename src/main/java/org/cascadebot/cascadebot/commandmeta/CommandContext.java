/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import lombok.AccessLevel;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.Checks;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.GuildSettingsCore;
import org.cascadebot.cascadebot.messaging.MessagingDirectMessage;
import org.cascadebot.cascadebot.messaging.MessagingTimed;
import org.cascadebot.cascadebot.messaging.MessagingTyped;
import org.cascadebot.cascadebot.messaging.MessagingUI;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.shared.Regex;

@Getter
public class CommandContext {

    private final GuildData data;
    private final ICommandExecutable command;

    private final JDA jda;
    private final TextChannel channel;
    private final Message message;
    private final Guild guild;
    private final Member member;

    private final String[] args;
    private final String trigger;
    private final boolean isMention;

    @Getter(AccessLevel.NONE)
    private final MessagingTyped messagingTyped = new MessagingTyped(this);

    @Getter(AccessLevel.NONE)
    private final MessagingDirectMessage messagingDirectMessage = new MessagingDirectMessage(this);

    @Getter(AccessLevel.NONE)
    private final MessagingUI messagingUI = new MessagingUI(this);

    @Getter(AccessLevel.NONE)
    private final MessagingTimed messagingTimed = new MessagingTimed(this);

    public CommandContext(ICommandExecutable command, JDA jda, TextChannel channel, Message message, Guild guild, GuildData data, String[] args, Member invoker,
                          String trigger, boolean isMention) {
        this.command = command;
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

    public GuildSettingsCore getCoreSettings() {
        return data.getCoreSettings();
    }

    public Locale getLocale() {
        return data.getLocale();
    }

    public CascadePlayer getMusicPlayer() {
        return CascadeBot.INS.getMusicHandler().getPlayer(guild.getIdLong());
    }

    public User getUser() {
        return member.getUser();
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

    /**
     * Tests for an argument of a particular id. This check it exists at the position and,
     * if the argument is a command arg, whether the localised command matches the input.
     *
     * @param id The argument id relative to the command
     * @return Whether the argument is present and correct in the arguments
     */
    public boolean testForArg(String id) {
        int requiredArgsCount = StringUtils.countMatches(id, '.') + 1;
        /*
            Tests to make sure that we're not trying to get an argument out of range

            For command of ;test <user> command
            If id given is user.command and args.length is 1 or 0, then the number of separators + 1
            (1 in this case) will be greater than to the number of args so we return false since
            there could not physically be an arg at that position.

            This guarantees there will always be an arg to check at the position.
            It's a lazy check because it doesn't check arguments after, that is the role of the command
            to check the arg length explicitly.
         */
        if (args.length < requiredArgsCount) return false;

        String argId = command.getAbsoluteCommand() + "." + id;
        Argument argument = CascadeBot.INS.getArgumentManager().getArgument(argId);
        // If the argument doesn't exist, it can't be valid!
        if (argument == null) return false;

        if (argument.getType() != ArgumentType.COMMAND) {
            // If it's not a command, we know that the arg exists so return true.
            return true;
        }

        return args[requiredArgsCount - 1].equalsIgnoreCase(argument.name(getLocale()));
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

    public String i18n(String path, Object... args) {
        return Language.i18n(guild.getIdLong(), path, args);
    }

    public String getUsage() {
        return getUsage(getCommand());
    }

    public String getUsage(ICommandExecutable command) {
        Argument parentArg = CascadeBot.INS.getArgumentManager().getArgument(command.getAbsoluteCommand());
        if (parentArg != null) {
            String parent = null;
            if (command instanceof ISubCommand) {
                parent = ((ISubCommand) command).parent();
            }
            String commandString = getCoreSettings().getPrefix() + (parent == null ? "" : parent + " ");
            return parentArg.getUsageString(getLocale(), commandString);
        } else {
            return "`" + getCoreSettings().getPrefix() + command.command(getLocale()) + "` - " + command.description(getLocale());
        }
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

    public void runOtherCommand(String command, Member sender, CommandContext context) {
        ICommandMain commandMain = CascadeBot.INS.getCommandManager().getCommandByDefault(command);
        if (hasPermission(commandMain.getPermission())) {
            commandMain.onCommand(member, context);
        } else {
            context.getUIMessaging().sendPermissionError(commandMain.getPermission());
        }
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
        return hasPermission(member, permission);
    }

    public boolean hasPermission(Member member, String permission) {
        CascadePermission cascadePermission = CascadeBot.INS.getPermissionsManager().getPermission(permission);
        if (cascadePermission == null) {
            CascadeBot.LOGGER.warn("Could not check permission {} as it does not exist!!", permission);
            return false;
        }
        return data.getPermissions().hasPermission(member, channel, cascadePermission, getCoreSettings());
    }

    public boolean hasPermission(CascadePermission permission) {
        return permission != null; // TODO: Check actual perms
    }

    //endregion


}
