/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import com.google.common.collect.ImmutableSet;
import net.dv8tion.jda.api.entities.Guild;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.PermissionNode;
import org.cascadebot.cascadebot.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PerGuildPermissionsManager {

    private HashMap<String, CascadePermission> guildPermissions = new HashMap<>();

    public PermissionsManager getCascadePermissionManager() {
        return CascadeBot.INS.getPermissionsManager();
    }

    // Method that registers all hte permissions after the data was loaded
    public void registerPermissions(GuildData data) {
        for (Map.Entry<String, Tag> entry : data.getManagement().getTags().entrySet()) {
            registerGuildPermission(entry.getValue().getInternalPermission());
        }
    }

    public boolean registerGuildPermission(CascadePermission permission) {
        if (guildPermissions.containsKey(permission.getPermissionRaw())) {
            return false;
        }
        guildPermissions.put(permission.getPermissionRaw(), permission);
        return true;
    }

    public CascadePermission getPermission(String permission) {
        CascadePermission cascadePermission = getCascadePermissionManager().getPermission(permission);
        if (cascadePermission != null) {
            return cascadePermission;
        }

        return guildPermissions.get(permission);
    }

    public CascadePermission getPermissionFromModule(Module module) {
        Map<String, CascadePermission> allPermissions = new HashMap<>(getCascadePermissionManager().getPermissionMap());
        allPermissions.putAll(guildPermissions);
        return allPermissions
                .values()
                .stream()
                .filter(permission -> permission.getModule().equals(module))
                .findFirst()
                .orElse(null); // Gets the permission connected to this module or returns null
    }

    public boolean isValidPermission(String permission) {
        Set<CascadePermission> permissions = getPermissions();
        if (permission.contains("*")) {
            PermissionNode node = new PermissionNode(permission);
            for (CascadePermission perm : permissions) {
                if (perm != CascadePermission.ALL_PERMISSIONS) {
                    if (node.test(perm.getPermissionRaw())) return true;
                }
            }
        }
        return getPermission(permission.substring(permission.startsWith("-") ? 1 : 0)) != null;
    }

    public Set<CascadePermission> getDefaultPermissions() {
        return getPermissions(true);
    }

    // TODO figure out how to handle localization

    public Set<CascadePermission> getPermissions() {
        return getPermissions(false);
    }

    public Set<CascadePermission> getPermissions(boolean defaultOnly) {
        Set<CascadePermission> guildPermissions = this.guildPermissions.values().stream().collect(ImmutableSet.toImmutableSet());
        if (defaultOnly) {
            Set<CascadePermission> permissions = getCascadePermissionManager().getDefaultPermissions();
            for (CascadePermission permission: guildPermissions) {
                if (permission.isDefaultPerm()) {
                    permissions.add(permission);
                }
            }
            return permissions;
        } else {
            Set<CascadePermission> permissions = new HashSet<>(getCascadePermissionManager().getPermissions());
            permissions.addAll(guildPermissions);
            return permissions;
        }
    }

}
