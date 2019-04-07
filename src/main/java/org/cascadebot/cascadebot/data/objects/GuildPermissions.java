/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.Security;
import org.cascadebot.cascadebot.permissions.objects.Group;
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

    public boolean hasPermission(Member member, Channel channel, CascadePermission permission) {

        if (Security.isAuthorised(
                CascadeBot.INS.getPermissionsManager().getUserSecurityLevel(member.getUser().getIdLong()),
                SecurityLevel.DEVELOPER
        )) return true;
        if (member.isOwner()) return true;
        if (member.hasPermission(Permission.ADMINISTRATOR)) return true;




        return false;
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
        return group;
    }




}
