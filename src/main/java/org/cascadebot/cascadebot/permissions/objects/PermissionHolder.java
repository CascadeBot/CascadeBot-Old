package org.cascadebot.cascadebot.permissions.objects;

import com.google.common.collect.Sets;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.PermissionNode;

import java.util.Set;

public abstract class PermissionHolder {

    private Set<String> permissions = Sets.newConcurrentHashSet();

    abstract HolderType getType();

    public Set<String> getPermissions() {
        return Set.copyOf(permissions);
    }

    public boolean addPermission(String permission) {
        return permissions.add(permission);
    }

    public boolean removePermission(String permission) {
        return permissions.remove(permission);
    }

    public Result evaluatePermission(CascadePermission permission) {
        for (String perm : getPermissions()) {
            if (new PermissionNode(perm.substring(perm.startsWith("-") ? 1 : 0)).test(permission.getPermissionNode())) {
                if (perm.startsWith("-"))
                    return PermissionAction.DENY.toResult(this);
                return PermissionAction.ALLOW.toResult(this);
            }
        }
        return PermissionAction.NEUTRAL.toResult(this);
    }

    enum HolderType {
        GROUP, USER
    }

}
