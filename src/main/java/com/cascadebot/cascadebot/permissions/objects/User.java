/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.permissions.objects;

import com.cascadebot.cascadebot.permissions.Permission;
import com.google.common.collect.Sets;

import java.util.Set;

public class User {

    private final Set<String> groups = Sets.newConcurrentHashSet();
    private final Set<Permission> permissions = Sets.newConcurrentHashSet();

    public boolean addGroup(Group group) {
        return groups.add(group.getName());
    }

    public boolean removeGroup(Group group) {
        return groups.remove(group.getName());
    }


    public Set<String> getGroups() {
        return groups;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

}
