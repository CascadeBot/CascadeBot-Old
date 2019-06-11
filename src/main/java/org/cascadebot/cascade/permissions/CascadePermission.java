/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.permissions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.dv8tion.jda.core.Permission;
import org.cascadebot.cascade.commandmeta.Module;

import java.util.Arrays;
import java.util.EnumSet;

@EqualsAndHashCode
@Getter
public class CascadePermission {

    public static final CascadePermission ALL_PERMISSIONS = CascadePermission.of("All permissions", "*");

    private final String label;
    private final String permission;
    private final boolean defaultPerm;
    private final EnumSet<Permission> discordPerm;
    private final Module module;


    private CascadePermission(String label, String permission, boolean defaultPerm, Module module, Permission... discordPerm) {
        this.label = label;
        this.permission = PermissionsManager.PERMISSION_PREFIX + permission;
        this.defaultPerm = defaultPerm;
        this.module = module;
        this.discordPerm = EnumSet.noneOf(Permission.class);
        this.discordPerm.addAll(Arrays.asList(discordPerm));
    }

    public static CascadePermission of(String permission) {
        return new CascadePermission(null, permission, false, null);
    }

    public static CascadePermission of(String label, String permission) {
        return new CascadePermission(label, permission, false, null);
    }

    public static CascadePermission of(String label, String permission, boolean defaultPerm) {
        return new CascadePermission(label, permission, defaultPerm, null);
    }

    public static CascadePermission of(String label, String permission, Module module) {
        return new CascadePermission(label, permission, false, module);
    }

    public static CascadePermission of(String label, String permission, boolean defaultPerm, Module module) {
        return new CascadePermission(label, permission, defaultPerm, module);
    }

    public static CascadePermission of(String label, String permission, boolean defaultPerm, Permission... discordPerm) {
        return new CascadePermission(label, permission, defaultPerm, null, discordPerm);
    }

    public static CascadePermission of(String label, String permission, boolean defaultPerm, Module module, Permission... discordPerm) {
        return new CascadePermission(label, permission, defaultPerm, module, discordPerm);
    }

    public EnumSet<Permission> getDiscordPerms() {
        return discordPerm;
    }

    @Override
    public String toString() {
        return getPermission();
    }

}
