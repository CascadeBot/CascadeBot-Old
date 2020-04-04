package org.cascadebot.cascadebot.data.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandFilter {

    @Getter
    private String name;

    @Getter
    @Setter
    private FilterType type = FilterType.BLACKLIST;

    @Getter
    @Setter
    private FilterOperator operator = FilterOperator.AND;

    @Getter
    @Setter
    private boolean enabled = false;

    private List<String> commands;
    private List<Long> channelIds;
    private List<Long> userIds;
    private List<Long> roleIds;

    public CommandFilter(String name) {
        this.name = name;
    }

    public void addChannel(long channelId) {
        if (channelIds == null) channelIds = Collections.synchronizedList(new ArrayList<>());
        this.channelIds.add(channelId);
    }

    public boolean removeChannel(long channelId) {
        if (channelIds == null) return false;
        return this.channelIds.remove(channelId);
    }

    public List<Long> getChannelIds() {
        return Collections.unmodifiableList(channelIds);
    }

    public void addUser(long userId) {
        if (userIds == null) userIds = Collections.synchronizedList(new ArrayList<>());
        this.userIds.add(userId);
    }

    public boolean removeUser(long userId) {
        if (userIds == null) return false;
        return this.userIds.remove(userId);
    }

    public List<Long> getUserIds() {
        return Collections.unmodifiableList(userIds);
    }

    public void addRole(long roleId) {
        if (roleIds == null) roleIds = Collections.synchronizedList(new ArrayList<>());
        this.roleIds.add(roleId);
    }

    public boolean removeRole(long roleId) {
        if (roleIds == null) return false;
        return this.roleIds.remove(roleId);
    }

    public List<Long> getRoleIds() {
        return Collections.unmodifiableList(roleIds);
    }

    public void addCommand(String command) {
        if (commands == null) commands = Collections.synchronizedList(new ArrayList<>());
        this.commands.add(command);
    }

    public boolean removeCommand(String command) {
        if (commands == null) return false;
        return this.commands.remove(command);
    }

    public List<String> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public FilterResult evaluateFilter(String command, TextChannel channel, Member member) {

        if (!enabled) {
            return FilterResult.NEUTRAL;
        }
        if (commands == null || !commands.contains(command)) {
            return FilterResult.NEUTRAL;
        }

        FilterMatch channelMatch = FilterMatch.NEUTRAL;
        // The channel condition is only considered if one or more channels have been added to the filter
        if (channelIds != null && channelIds.size() != 0) {
            channelMatch = channelIds.contains(channel.getIdLong()) ? FilterMatch.MATCH : FilterMatch.NOT_MATCH;
        }

        FilterMatch userMatch = FilterMatch.NEUTRAL;
        // The user condition is only considered if one or more users have been added to the filter
        if (userIds != null && userIds.size() != 0) {
            userMatch = userIds.contains(member.getIdLong()) ? FilterMatch.MATCH : FilterMatch.NOT_MATCH;
        }

        FilterMatch roleMatch = FilterMatch.NEUTRAL;
        // The role condition is only considered if one or more roles have been added to the filter
        if (roleIds != null && roleIds.size() != 0) {
            roleMatch = member.getRoles().stream().map(Role::getIdLong).anyMatch(id -> roleIds.contains(id)) ? FilterMatch.MATCH : FilterMatch.NOT_MATCH;
        }

        boolean combinedResult;

        if (operator == FilterOperator.AND) {
            // Check that all of the results are either MATCH or NEUTRAL
            combinedResult = channelMatch != FilterMatch.NOT_MATCH &&
                    userMatch != FilterMatch.NOT_MATCH &&
                    roleMatch != FilterMatch.NOT_MATCH;
        } else {
            // Check that any of the results are either MATCH or NEUTRAL
            combinedResult = channelMatch != FilterMatch.NOT_MATCH ||
                    userMatch != FilterMatch.NOT_MATCH ||
                    roleMatch != FilterMatch.NOT_MATCH;
        }

        switch (type) {
            case WHITELIST:
                return combinedResult ? FilterResult.ALLOW : FilterResult.DENY;
            case BLACKLIST:
                return combinedResult ? FilterResult.DENY : FilterResult.ALLOW;
        }

        return FilterResult.NEUTRAL;
    }


    /**
     * Determines whether users who match this filter will be blocked or whitelisted
     */
    public enum FilterType {
        WHITELIST, BLACKLIST
    }


    /**
     * Determines whether the channel, roles and user properties all have to be matched or just one of them
     */
    public enum FilterOperator {
        AND, OR
    }

    public enum FilterResult {
        ALLOW, DENY, NEUTRAL
    }

    public enum FilterMatch {
        MATCH, NOT_MATCH, NEUTRAL
    }

}
