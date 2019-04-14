/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.utils.Checks;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.Environment;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.Security;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.permissions.objects.PermissionAction;
import org.cascadebot.cascadebot.permissions.objects.PermissionMode;
import org.cascadebot.cascadebot.permissions.objects.Result;
import org.cascadebot.cascadebot.permissions.objects.ResultCause;
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

    private PermissionMode mode = PermissionMode.HIERARCHICAL;

    private List<Group> groups = Collections.synchronizedList(new ArrayList<>());
    private Map<Long, User> users = new ConcurrentHashMap<>();

    public Result hasPermission(Member member, CascadePermission permission, GuildSettings settings) {
        return hasPermission(member, null, permission, settings);
    }

    public Result hasPermission(Member member, Channel channel, CascadePermission permission, GuildSettings settings) {

        Checks.notNull(member, "member");
        Checks.notNull(permission, "permission");

        // This allows developers and owners to go into guilds and fix problems
        if (Security.isAuthorised(member.getUser().getIdLong(), SecurityLevel.DEVELOPER)) {
            return new Result(PermissionAction.ALLOW, ResultCause.OFFICIAL);
        }
        if (Security.isAuthorised(member.getUser().getIdLong(), SecurityLevel.CONTRIBUTOR) && Environment.isDevelopment()){
            return new Result(PermissionAction.ALLOW, ResultCause.OFFICIAL);
        }
        // If the user is owner then they have all perms, obsv..
        if (member.isOwner()) return new Result(PermissionAction.ALLOW, ResultCause.GUILD);
        // By default all members with the administrator perm have access to all perms; this can be turned off
        if (member.hasPermission(Permission.ADMINISTRATOR) && settings.doAdminsHaveAllPerms()) {
            return new Result(PermissionAction.ALLOW, ResultCause.GUILD);
        }

        User user = users.computeIfAbsent(member.getUser().getIdLong(), id -> new User());
        // Get all user groups that are directly assigned and the groups assigned through roles
        List<Group> userGroups = getUserGroups(member);

        Result result = getDefaultAction(permission);
        Result evaluatedResult = new Result(PermissionAction.NEUTRAL);

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
        if (result.isNeutral() && hasDiscordPermissions(member, channel, permission.getDiscordPerm())) {
            result = new Result(PermissionAction.ALLOW, ResultCause.DISCORD);
        }

        return result;
    }

    private boolean hasDiscordPermissions(Member member, Channel channel, Set<Permission> permissions) {
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
        Result result = new Result(PermissionAction.NEUTRAL);
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
                new Result(PermissionAction.ALLOW, ResultCause.DEFAULT) :
                new Result(PermissionAction.DENY, ResultCause.DEFAULT);
    }

    public Group createGroup(String name) {
        Set<String> ids = groups.stream().map(Group::getId).collect(Collectors.toSet());
        Group group;
        int iterations = 0;
        do {
            group = new Group(name);
            if (++iterations == 7) {
                // If this happens then... run?
                CascadeBot.LOGGER.error("Somehow we couldn't manage to create a unique guild ID :(");
                throw new IllegalStateException("Could not create a group with a unique ID!");
            }
        } while (ids.contains(group.getId()));
        groups.add(group);
        return group;
    }

    public boolean deleteGroup(String id) {
        return groups.removeIf(group -> group.getId().equals(id));
    }

    public List<Group> getUserGroups(Member member) {
        User user = users.computeIfAbsent(member.getUser().getIdLong(), id -> new User());
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

}
