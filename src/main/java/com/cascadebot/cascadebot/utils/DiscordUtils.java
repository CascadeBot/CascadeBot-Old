/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import com.cascadebot.cascadebot.CascadeBot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.Checks;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Checks.notNull(guild, "guild");
        String id = null;
        if (idPattern.matcher(search).matches()) {
            id = search;
        }
        Matcher matcher = userMentionPattern.matcher(search);
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

    private static User getUserById(String id) {
        return CascadeBot.instance().getClient().getUserById(id);
    }

    /**
     * Gets the username#discrim for the specified {@link User}.
     *
     * @param user The {@link User} to get tag from.
     * @return The username#discrim for the specified {@link User}.
     * @throws IllegalArgumentException if the {@link User} is null.
     */
    public static String getTag(User user) {
        Checks.notNull(user, "user");
        return user.getName() + "#" + user.getDiscriminator();
    }

    /**
     *
     *
     * @param search The string to find the {@link Role} with.
     * @param guild  The {@link Guild} to fnd the {@link Role} in.
     * @return The {@link Role} found or null if no role was found with the search.
     * @throws IllegalArgumentException if search is null
     */
    public static Role getRole(String search, Guild guild) {
        Checks.notBlank(search, "role");
        Checks.notNull(guild, "guild");
        String id = null;
        if (idPattern.matcher(search).matches()) {
            id = search;
        }
        Matcher matcher = roleMentionPattern.matcher(search);
        if (matcher.matches()) {
            id = matcher.group(1);
        }

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
}
