/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.data.Config;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.Checks;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DiscordUtils {

    private static final Pattern idPattern = Pattern.compile("[0-9]{17,}");
    private static final Pattern userMentionPattern = Pattern.compile("<@!?([0-9]{17,})>");
    private static final Pattern roleMentionPattern = Pattern.compile("<@&([0-9]{17,})>");

    /**
     * Attempts to find a member using a string input.
     * The string can be their id, a mention, or a name.
     *
     * @param search The string to find the {@link Member} with.
     * @param guild  The {@link Guild} to fnd the {@link Member} in.
     * @return The {@link Member} found or null if no member was found with the search.
     * @throws IllegalArgumentException if search is null.
     */
    public static Member getMember(String search, Guild guild) {
        Checks.notBlank(search, "user");
        String id = getIdFromString(search, guild, userMentionPattern);

        if (id != null) {
            User user = getUserById(Long.parseLong(id));
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

    private static User getUserById(Long userId) {
        return CascadeBot.INS.getShardManager().getUserById(userId);
    }

    private static Guild getGuildById(Long guildId) {
        return CascadeBot.INS.getShardManager().getGuildById(guildId);
    }

    public static MessageChannel getTextChannelById(Long channelId) {
        return CascadeBot.INS.getShardManager().getTextChannelById(channelId);
    }

    /**
     * @param search The string to find the {@link Role} with.
     * @param guild  The {@link Guild} to fnd the {@link Role} in.
     * @return The {@link Role} found or null if no role was found with the search.
     * @throws IllegalArgumentException if search is null
     */
    public static Role getRole(String search, Guild guild) {
        Checks.notBlank(search, "role");
        String id = getIdFromString(search, guild, roleMentionPattern);

        if (id != null) {
            Role role = guild.getRoleById(id);
            if (role != null) {
                return role; //I'm returning here in case for some reasons the role name looks like a id.
            }
        }

        List<Role> roles = guild.getRolesByName(search, true);
        if (roles.size() == 0) {
            return null;
        } else if (roles.size() == 1) {
            return roles.get(0);
        } else {
            //TODO maybe add an option to get roles from a group with buttons?
            return null;
        }
        //TODO Combine these methods so intellij stops complaining about duplicate code.
    }

    public static Set<Role> getAllRoles(Member member) {
        return Set.copyOf(member.getRoles());
    }

    public static Set<Long> getAllRoleIds(Member member) {
        return getAllRoles(member).stream().map(Role::getIdLong).collect(Collectors.toSet());
    }

    public static Set<Long> getAllOfficialRoleIds(long userID) {
        if (Config.INS.getOfficialServerId() != -1 && getOfficialGuild() != null) {
            return getAllRoleIds(getOfficialGuild().getMemberById(userID));
        } else {
            return Set.of();
        }
    }

    public static Guild getOfficialGuild() {
        return getGuildById(Config.INS.getOfficialServerId());
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
     * Checks the permission for the member and channel provided for the context.
     * Usually this is the channel a command was sent in and the member who send the command.
     *
     * @param permissions Non-null and non empty permissions to check.
     * @return true if the member has all of the specified permissions in the channel.
     * @throws IllegalArgumentException if permissions are empty or null.
     * @throws IllegalArgumentException if member is null or not in the same guild.
     */
    public boolean hasPermission(Member member, Channel channel, Permission... permissions) {
        Checks.notEmpty(permissions, "Permissions");
        Checks.check(member.getGuild().getIdLong() == channel.getGuild().getIdLong(), "Member and channel need to be in the same guild!");
        return member.hasPermission(channel, permissions);
    }

    private static String getIdFromString(String search, Guild guild, Pattern pattern) {
        Checks.notNull(guild, "guild");
        String id = null;
        if (idPattern.matcher(search).matches()) {
            id = search;
        }
        Matcher matcher = pattern.matcher(search);
        if (matcher.matches()) {
            id = matcher.group(1);
        }

        return id;
    }
}
