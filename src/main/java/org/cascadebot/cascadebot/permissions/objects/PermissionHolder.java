package org.cascadebot.cascadebot.permissions.objects;

import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.PermissionNode;

import java.util.Set;

@ToString
@EqualsAndHashCode
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
            if (new PermissionNode(perm.substring(perm.startsWith("-") ? 1 : 0)).test(permission.getPermission())) {
                if (perm.startsWith("-"))
                    return Result.of(PermissionAction.DENY, this);
                return Result.of(PermissionAction.ALLOW, this);
            }
        }
        return Result.of(PermissionAction.NEUTRAL, this);
    }

    enum HolderType {
        GROUP, USER
    }

}
