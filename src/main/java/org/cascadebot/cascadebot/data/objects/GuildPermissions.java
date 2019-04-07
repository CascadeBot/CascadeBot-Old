/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import com.google.common.collect.Sets;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.permissions.objects.PermissionMode;
import org.cascadebot.cascadebot.permissions.objects.User;

import java.util.Set;
import java.util.stream.Collectors;

public class GuildPermissions {

    private PermissionMode mode = PermissionMode.HIERARCHICAL;

    private Set<Group> groups = Sets.newConcurrentHashSet();
    private Set<User> users = Sets.newConcurrentHashSet();

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
