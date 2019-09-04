/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.internal.utils.Checks;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.Environment;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.Security;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.permissions.objects.PermissionAction;
import org.cascadebot.cascadebot.permissions.objects.Result;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.shared.SecurityLevel;
import spark.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GuildPermissions {

    @Getter
    @Setter
    private PermissionMode mode = PermissionMode.MOST_RESTRICTIVE;

    private List<Group> groups = Collections.synchronizedList(new ArrayList<>());
    private Map<Long, User> users = new ConcurrentHashMap<>();

    public boolean hasPermission(Member member, CascadePermission permission, GuildSettingsCore settings) {
        return hasPermission(member, null, permission, settings);
    }

    public boolean hasPermission(Member sender, GuildChannel channel, CascadePermission permission, GuildSettingsCore settings) {
        return evalPermission(sender, channel, permission, settings).isAllowed();
    }

    public Result evalPermission(Member member, CascadePermission permission, GuildSettingsCore settings) {
        return evalPermission(member, null, permission, settings);
    }

    public Result evalPermission(Member member, GuildChannel channel, CascadePermission permission, GuildSettingsCore settings) {

        Checks.notNull(member, "member");
        Checks.notNull(permission, "permission");

        // This allows developers and owners to go into guilds and fix problems
        if (Security.isAuthorised(member.getIdLong(), SecurityLevel.DEVELOPER)) {
            return Result.of(PermissionAction.ALLOW, Result.ResultCause.OFFICIAL, SecurityLevel.DEVELOPER);
        }
        if (Security.isAuthorised(member.getIdLong(), SecurityLevel.CONTRIBUTOR) && Environment.isDevelopment()) {
            return Result.of(PermissionAction.ALLOW, Result.ResultCause.OFFICIAL, SecurityLevel.CONTRIBUTOR);
        }
        // If the user is owner then they have all perms, obsv..
        if (member.isOwner()) return Result.of(PermissionAction.ALLOW, Result.ResultCause.GUILD);
        // By default all members with the administrator perm have access to all perms; this can be turned off
        if (member.hasPermission(Permission.ADMINISTRATOR) && settings.isAdminsHaveAllPerms()) {
            return Result.of(PermissionAction.ALLOW, Result.ResultCause.GUILD);
        }

        User user = users.computeIfAbsent(member.getIdLong(), id -> new User());
        // Get all user groups that are directly assigned and the groups assigned through roles
        List<Group> userGroups = getUserGroups(member);

        Result result = getDefaultAction(permission);
        Result evaluatedResult = Result.of(PermissionAction.NEUTRAL);

        if (mode == PermissionMode.MOST_RESTRICTIVE) {
            evaluatedResult = evaluateMostRestrictiveMode(user, userGroups, permission);
        } else if (mode == PermissionMode.HIERARCHICAL) {
            evaluatedResult = evaluateHierarchicalMode(user, userGroups, permission);
        }

        if (!evaluatedResult.isNeutral()) {
            result = evaluatedResult;
        }

        // Discord permissions will only allow a permission if is not already allowed or denied.
        // It will not override Cascade permissions!
        if (result.isNeutral() && hasDiscordPermissions(member, channel, permission.getDiscordPerms())) {
            result = Result.of(PermissionAction.ALLOW, Result.ResultCause.DISCORD, permission.getDiscordPerms());
        }

        return result;
    }

    private boolean hasDiscordPermissions(Member member, GuildChannel channel, Set<Permission> permissions) {
        if (CollectionUtils.isEmpty(permissions)) return false;
        if (channel != null) {
            return member.hasPermission(channel, permissions);
        } else {
            return member.hasPermission(permissions);
        }
    }

    private Result evaluateMostRestrictiveMode(User user, List<Group> userGroups, CascadePermission permission) {
        Result result = user.evaluatePermission(permission);
        if (result.isDenied()) return result;

        for (Group group : userGroups) {
            Result groupResult = group.evaluatePermission(permission);
            // If the result is neutral, it has no effect on the existing result.
            if (groupResult.isNeutral()) continue;
            // This is most restrictive mode so if any group permissions is DENY, the evaluated result is DENY.
            if (groupResult.isDenied()) return groupResult;
            result = groupResult;
        }
        return result;
    }

    private Result evaluateHierarchicalMode(User user, List<Group> userGroups, CascadePermission permission) {
        Result result = Result.of(PermissionAction.NEUTRAL);
        // Loop through the groups backwards to preserve hierarchy; groups higher up override lower groups.
        for (int i = userGroups.size() - 1; i >= 0; i--) {
            Result groupResult = userGroups.get(i).evaluatePermission(permission);
            if (groupResult.isNeutral()) continue;
            // This overrides any previous action with no regard to what it was.
            result = groupResult;
        }

        Result userResult = user.evaluatePermission(permission);
        // User permissions take ultimate precedence over group permissions
        if (!userResult.isNeutral()) {
            result = userResult;
        }

        return result;
    }

    private Result getDefaultAction(CascadePermission permission) {
        // A default permission will never explicitly deny a permission.
        return permission.isDefaultPerm() ?
                Result.of(PermissionAction.ALLOW, Result.ResultCause.DEFAULT) :
                Result.of(PermissionAction.DENY, Result.ResultCause.DEFAULT);
    }

    public Group createGroup(String name) {
        Set<String> ids = groups.stream().map(Group::getId).collect(Collectors.toSet());
        Group group;
        int iterations = 0;
        do {
            group = new Group(name); //TODO maybe not allow groups with the same name
            if (++iterations == 7) {
                // If this happens then... run?
                CascadeBot.LOGGER.error("Somehow we couldn't manage to create a unique group ID :(");
                throw new IllegalStateException("Could not create a group with a unique ID!");
            }
        } while (ids.contains(group.getId()));
        groups.add(group);
        return group;
    }

    public boolean deleteGroup(String id) {
        return groups.removeIf(group -> group.getId().equals(id));
    }

    public List<Group> getGroupsByName(String name) {
        List<Group> groups = new ArrayList<>();
        for (Group group : this.groups) {
            if (group.getName().equals(name)) {
                groups.add(group);
            }
        }
        return groups;
    }

    public Group getGroupById(String id) {
        for (Group group : this.groups) {
            if (group.getId().equals(id)) {
                return group;
            }
        }

        return null;
    }

    public User getPermissionUser(Member member) {
        return users.computeIfAbsent(member.getIdLong(), id -> new User());
    }

    public List<Group> getUserGroups(Member member) {
        User user = users.computeIfAbsent(member.getIdLong(), id -> new User());
        List<Group> userGroups = groups.stream().filter(group -> user.getGroupIds().contains(group.getId())).collect(Collectors.toList());

        // Now I know this is a mess... If you can figure out a better method hit me up 👀
        // This adds all the groups which have a id representing a role the member has.
        groups.stream()
                .filter(group -> group.getRoleIds().stream().anyMatch(id -> member.getRoles().contains(member.getGuild().getRoleById(id))))
                .forEach(userGroups::add);
        return userGroups;
    }

    public List<Group> getGroups() {
        return List.copyOf(groups);
    }

    public void moveGroup(Group group, int position) throws IndexOutOfBoundsException {
        groups.remove(group);
        groups.add(position, group);
    }

    public enum PermissionMode {

        HIERARCHICAL,
        MOST_RESTRICTIVE

    }

}
