/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.permissions.objects;

import com.google.common.collect.Sets;

import java.util.Set;

public class User extends PermissionHolder {

    private final Set<String> groups = Sets.newConcurrentHashSet();

    public boolean addGroup(Group group) {
        return groups.add(group.getId());
    }

    public boolean removeGroup(Group group) {
        return groups.remove(group.getId());
    }

    public Set<String> getGroupIds() {
        return Set.copyOf(groups);
    }

    @Override
    HolderType getType() {
        return HolderType.USER;
    }
}
