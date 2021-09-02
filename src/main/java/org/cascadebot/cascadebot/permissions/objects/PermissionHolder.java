package org.cascadebot.cascadebot.permissions.objects;

import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.cascadebot.cascadebot.data.database.BsonObject;
import org.cascadebot.cascadebot.data.objects.PermissionAction;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode;

@ToString
@EqualsAndHashCode
public abstract class PermissionHolder implements BsonObject {

    private Set<String> permissions = Sets.newConcurrentHashSet();

    abstract HolderType getType();

    public Set<String> getPermissions() {
        return Set.copyOf(permissions);
    }

    public boolean addPermission(String permission) {
        assertWriteMode();
        return permissions.add(permission);
    }

    public boolean removePermission(String permission) {
        assertWriteMode();
        return permissions.remove(permission);
    }

    public Result evaluatePermission(CascadePermission permission) {
        for (String perm : getPermissions()) {
            if (new PermissionNode(perm.substring(perm.startsWith("-") ? 1 : 0)).test(permission.getPermissionRaw())) {
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

    @Override
    public void fromBson(@NotNull BsonDocument bsonDocument) {
        if (bsonDocument.containsKey("permissions")) {
            permissions.clear();
            for (BsonValue permBson : bsonDocument.get("permissions").asArray()) {
                permissions.add(permBson.asString().getValue());
            }
        }
    }
}
