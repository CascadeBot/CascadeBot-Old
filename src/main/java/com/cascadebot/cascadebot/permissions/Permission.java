/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.permissions;

import com.cascadebot.cascadebot.commandmeta.CommandType;

import java.util.Arrays;
import java.util.EnumSet;

public class Permission {

    private final String label;
    private final String permission;
    private final boolean defaultPerm;
    private final EnumSet<net.dv8tion.jda.core.Permission> discordPerm;
    private final CommandType commandType;


    private Permission(String label, String permission, boolean defaultPerm, CommandType commandType, net.dv8tion.jda.core.Permission... discordPerm) {
        this.label = label;
        this.permission = "cascade." + permission;
        this.defaultPerm = defaultPerm;
        this.commandType = commandType;
        this.discordPerm = EnumSet.noneOf(net.dv8tion.jda.core.Permission.class);
        this.discordPerm.addAll(Arrays.asList(discordPerm));
    }

    public static Permission of(String permission) {
        return new Permission(null, permission, false, null);
    }

    public static Permission of(String label, String permission) {
        return new Permission(label, permission, false, null);
    }

    public static Permission of(String label, String permission, boolean defaultPerm) {
        return new Permission(label, permission, defaultPerm, null);
    }

    public static Permission of(String label, String permission, CommandType commandType) {
        return new Permission(label, permission, false, commandType);
    }

    public static Permission of(String label, String permission, boolean defaultPerm, CommandType commandType) {
        return new Permission(label, permission, defaultPerm, commandType);
    }

    public static Permission of(String label, String permission, boolean defaultPerm, net.dv8tion.jda.core.Permission... discordPerm) {
        return new Permission(label, permission, defaultPerm, null, discordPerm);
    }


    public String getPermissionNode() {
        return permission;
    }

    public boolean isDefaultPerm() {
        return defaultPerm;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    /*public static Permission getPermission(CommandType commandType) {
        return COMMAND_TYPE_MAP.get(commandType);
    }

    public static Permission getPermission(String permission) {
        return PERMISSION_MAP.get(permission.toLowerCase());
    }

    public static boolean isValidPermission(String permission) {
        if (permission.contains("*") && permission.contains(".")) {
            PermissionNode node = new PermissionNode(permission);
            for (Permission perm : Permission.VALUES) {
                if (perm != Permission.ALL_PERMISSIONS) {
                    if (node.test(perm.getPermissionNode())) return true;
                }
            }
        }
        return getPermission(permission.substring(permission.startsWith("-") ? 1 : 0)) != null;
    }

    public EnumSet<net.dv8tion.jda.core.Permission> getDiscordPerm() {
        return discordPerm;
    }*/ // TODO: Move this to permissions manager

    @Override
    public String toString() {
        return getPermissionNode();
    }

}
