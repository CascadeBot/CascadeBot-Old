package com.cascadebot.cascadebot.utils;

import com.cascadebot.cascadebot.CascadeBot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.Checks;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordUtils {

    private static final Pattern idPattern = Pattern.compile("[0-9]{18}");
    private static final Pattern mentionPattern = Pattern.compile("<@([0-9]{18})>");

    /**
     * Attempts to find a member using a string input.
     * The string can be their id, a mention, or a name.
     *
     * @param search The string to find the {@link Member} with.
     * @return The {@link Member} found or null if no member was found with that name.
     * @throws IllegalArgumentException if name is null.
     */
    public static Member getMember(String search, Guild guild) {
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
}
