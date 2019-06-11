/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.Checks;
import org.cascadebot.cascadebot.Cascade;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.shared.Regex;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class DiscordUtils {

    //region Members and Users

    /**
     * Attempts to find a member using a string input.
     * The string can be their id, a mention, or a name.
     *
     * @param search The string to find the {@link Member} with.
     * @param guild  The {@link Guild} to fnd the {@link Member} in.
     * @return The {@link Member} found or null if no member was found with the search.
     * @throws IllegalArgumentException if search is null or blank.
     */
    public static Member getMember(Guild guild, String search) {
        List<Member> members = FinderUtil.findMembers(search, guild);
        Member targetMember = null;

        if (members.size() > 1) {
            /*context.replyDanger("There is more than one user that matches this criterion! Please enter the ID or the user's full name!");*/
        } else {
            targetMember = members.size() != 1 ? null : members.get(0);
        }
        return targetMember;
    }

    /**
     * Attempts to find a member using a string input.
     * This first checks the guilds the bot is in and
     * then attempts to retrieve the user from Discord
     * if initially unsuccessful.
     * <p>
     * The string can be their id or a mention.
     *
     * @param search   The string to find the {@link User} with.
     * @param retrieve Whether to request the user from discord if none of our guilds has them in.
     *                 This causes an extra request to be sent off so it will be slower if this is enabled!
     * @return The {@link User} found or null if no user was found with the search.
     * @throws IllegalArgumentException if search is null or blank.
     */
    public static User getUser(Guild guild, String search, boolean retrieve) {
        Checks.notBlank(search, "user");
        List<User> users = FinderUtil.findUsers(search, guild.getJDA());
        if (users.size() > 1) {
            /*context.replyDanger("There is more than one user that matches this criterion! Please enter the ID or the user's full name!");*/
            return null;
        }
        User user = users.size() != 1 ? null : users.get(0);
        if (user == null && Regex.ID.matcher(search).matches() && retrieve) {
            user = Cascade.INS.getShardManager().retrieveUserById(Long.valueOf(search)).complete();
        }
        return user;
    }

    private static User getUserById(Long userId) {
        return Cascade.INS.getShardManager().getUserById(userId);
    }
    //endregion

    private static Guild getGuildById(Long guildId) {
        return Cascade.INS.getShardManager().getGuildById(guildId);
    }

    public static MessageChannel getTextChannelById(Long channelId) {
        return Cascade.INS.getShardManager().getTextChannelById(channelId);
    }

    //region Roles

    /**
     * @param search The string to find the {@link Role} with.
     * @param guild  The {@link Guild} to fnd the {@link Role} in.
     * @return The {@link Role} found or null if no role was found with the search.
     * @throws IllegalArgumentException if search is null
     */
    public static Role getRole(String search, Guild guild) {
        Checks.notBlank(search, "role");

        List<Role> roles = FinderUtil.findRoles(search, guild);
        if (roles.size() == 0) {
            return null;
        } else if (roles.size() == 1) {
            return roles.get(0);
        } else {
            //TODO maybe add an option to get roles from a group with buttons?
            return null;
        }
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
    //endregion

    public static Guild getOfficialGuild() {
        return getGuildById(Config.INS.getOfficialServerId());
    }

    /**
     * Gets an id from a string using a pattern
     *
     * @param search  The string to search in
     * @param pattern The patten to use to look for the id. The id should be in group 1 (I'll expand this later)
     * @return The id in form of a string
     */
    private static String getIdFromString(String search, Pattern pattern) {
        String id = null;
        if (Regex.ID.matcher(search).matches()) {
            id = search;
        }
        Matcher matcher = pattern.matcher(search);
        if (matcher.matches()) {
            id = matcher.group(1);
        }

        return id;
    }

}
