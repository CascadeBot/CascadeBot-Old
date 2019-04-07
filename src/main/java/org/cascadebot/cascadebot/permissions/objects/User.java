/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.permissions.objects;

import com.google.common.collect.Sets;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.PermissionNode;

import java.util.Set;

public class User {

    private final Set<String> groups = Sets.newConcurrentHashSet();
    private final Set<String> permissions = Sets.newConcurrentHashSet();

    public boolean addGroup(Group group) {
        return groups.add(group.getId());
    }

    public boolean removeGroup(Group group) {
        return groups.remove(group.getId());
    }

    public Set<String> getGroupIds() {
        return Set.copyOf(groups);
    }

    public Set<String> getPermissions() {
        return Set.copyOf(permissions);
    }

    public boolean addPermission(String permission) {
        return permissions.add(permission);
    }

    public boolean removePermission(String permission) {
        return permissions.remove(permission);
    }

    public PermissionAction getPermissionAction(CascadePermission permission) {
        for (String perm : permissions) {
            if (new PermissionNode(perm.substring(perm.startsWith("-") ? 1 : 0)).test(permission.getPermissionNode())) {
                if (perm.startsWith("-"))
                    return PermissionAction.DENY;
                return PermissionAction.ALLOW;
            }
        }
        return PermissionAction.NEUTRAL;
    }

}
