/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.Environment;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.Security;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.permissions.objects.PermissionAction;
import org.cascadebot.cascadebot.permissions.objects.PermissionMode;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.shared.SecurityLevel;

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

    public boolean hasPermission(Member member, CascadePermission permission) {
        return hasPermission(member, null, permission);
    }

    public boolean hasPermission(Member member, Channel channel, CascadePermission permission) {

        if (Security.isAuthorised(member.getUser().getIdLong(), SecurityLevel.DEVELOPER)) return true;
        if (Security.isAuthorised(member.getUser().getIdLong(), SecurityLevel.CONTRIBUTOR) && Environment.isDevelopment()) return true;
        if (member.isOwner()) return true;
        if (member.hasPermission(Permission.ADMINISTRATOR)) return true;

        User user = users.computeIfAbsent(member.getUser().getIdLong(), id -> new User());
        List<Group> userGroups = groups.stream().filter(group -> user.getGroupIds().contains(group.getId())).collect(Collectors.toList());

        PermissionAction action = getDefaultAction(permission);
        PermissionAction evaluatedAction = PermissionAction.NEUTRAL;

        if (mode == PermissionMode.MOST_RESTRICTIVE) {
            evaluatedAction = evaluateMostRestrictiveMode(user, userGroups, permission);
        } else if (mode == PermissionMode.HIERARCHICAL) {
            evaluatedAction = evaluateHierarchicalMode(user, userGroups, permission);
        }

        if (evaluatedAction != PermissionAction.NEUTRAL) {
            action = evaluatedAction;
        }

        return action == PermissionAction.ALLOW;
    }

    private PermissionAction evaluateMostRestrictiveMode(User user, List<Group> userGroups, CascadePermission permission) {
        PermissionAction action = user.getPermissionAction(permission);
        if (action == PermissionAction.DENY) return action;

        for (Group group : userGroups) {
            PermissionAction groupAction = group.getPermissionAction(permission);
            if (groupAction == PermissionAction.NEUTRAL) continue;
            if (groupAction == PermissionAction.DENY) return PermissionAction.DENY;
            action = groupAction;
        }
        return action;
    }

    private PermissionAction evaluateHierarchicalMode(User user, List<Group> userGroups, CascadePermission permission) {
        PermissionAction action = PermissionAction.NEUTRAL;
        for (int i = userGroups.size() - 1; i >= 0; i--) {
            PermissionAction groupAction = userGroups.get(i).getPermissionAction(permission);
            if (groupAction == PermissionAction.NEUTRAL) continue;
            action = groupAction;
        }

        PermissionAction userAction = user.getPermissionAction(permission);
        if (userAction != PermissionAction.NEUTRAL) {
            action = userAction;
        }

        return action;
    }

    private PermissionAction getDefaultAction(CascadePermission permission) {
        return permission.isDefaultPerm() ? PermissionAction.ALLOW : PermissionAction.NEUTRAL;
    }

    public Group createGroup(String name) {
        Set<String> ids = groups.stream().map(Group::getId).collect(Collectors.toSet());
        Group group;
        int iterations = 0;
        do {
            group = new Group(name);
            if (++iterations == 7) {
                // If this happens then... run?
                throw new IllegalStateException("Could not create a group with a unique ID!");
            }
        } while (ids.contains(group.getId()));
        groups.add(group);
        return group;
    }


}
