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
    private FilterType type;

    @Getter
    @Setter
    private FilterOperator operator;

    private List<String> commands;
    private List<Long> channelIds;
    private List<Long> userIds;
    private List<Long> roleIds;

    public CommandFilter(String name, FilterType type) {
        this.name = name;
        this.type = type;
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

        if (!commands.contains(command)) return FilterResult.NEUTRAL;

        boolean channelResult = channelIds != null && channelIds.contains(channel.getIdLong());
        boolean userResult = userIds != null && userIds.contains(member.getIdLong());
        boolean roleResult = roleIds != null && member.getRoles().stream().map(Role::getIdLong).anyMatch(id -> roleIds.contains(id));

        boolean combinedResult;

        if (operator == FilterOperator.AND){
            combinedResult = channelResult && userResult && roleResult;
        } else {
            combinedResult = channelResult || userResult || roleResult;
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

}
