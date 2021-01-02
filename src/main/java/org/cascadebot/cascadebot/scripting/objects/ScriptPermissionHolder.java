package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class ScriptPermissionHolder extends ScriptSnowflake {

    protected IPermissionHolder internalPermissionHolder;

    public ScriptPermissionHolder(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public List<Permission> getPermissions() {
        return new ArrayList<>(internalPermissionHolder.getPermissions());
    }

    public List<Permission> getPermissions(ScriptChannel channel) {
        return new ArrayList<>(internalPermissionHolder.getPermissions(channel.internalChannel));
    }

    public List<Permission> getPermissionsExplicit() {
        return new ArrayList<>(internalPermissionHolder.getPermissionsExplicit());
    }

    public List<Permission> getPermissionsExplicit(ScriptChannel channel) {
        return new ArrayList<>(internalPermissionHolder.getPermissionsExplicit(channel.internalChannel));
    }

    public boolean hasPermission(Permission permission) {
        return internalPermissionHolder.hasPermission(permission);
    }

    public boolean hasPermission(Permission permission, ScriptChannel channel) {
        return internalPermissionHolder.hasPermission(channel.internalChannel, permission);
    }

    public boolean hasAccess(ScriptChannel channel) {
        return internalPermissionHolder.hasAccess(channel.internalChannel);
    }

    public static ScriptPermissionHolder fromJda(ScriptContext scriptContext, IPermissionHolder holder) {
        if (holder instanceof Member) {
            ScriptUser scriptUser = new ScriptUser(scriptContext);
            scriptUser.setInternalUser((Member) holder);
            return scriptUser;
        } else if (holder instanceof Role) {
            ScriptRole scriptRole = new ScriptRole(scriptContext);
            scriptRole.setInternalRole((Role) holder);
            return scriptRole;
        } else {
            return null;
        }
    }

}
