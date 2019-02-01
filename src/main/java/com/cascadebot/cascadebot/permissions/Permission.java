/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.permissions;

import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.utils.CollectionUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

public enum Permission {

    ALL_PERMISSIONS("*"),

    INFO_CATEGORY("category.info", true, CommandType.INFORMATIONAL),

    SERVER_INFO_COMMAND("serverinfo", true),
    USER_INFO_COMMAND("userinfo", true)


    ;

    public static final Permission[] VALUES = Permission.values();

    private String permission;
    private boolean defaultPerm;
    private EnumSet<net.dv8tion.jda.core.Permission> discordPerm = EnumSet.noneOf(net.dv8tion.jda.core.Permission.class);
    private CommandType commandType;

    private static final Map<CommandType, Permission> COMMAND_TYPE_MAP = CollectionUtils.getReverseMapping(
                    Permission.class,
                    Permission::getCommandType);
    private static final Map<String, Permission> PERMISSION_MAP = CollectionUtils.getReverseMapping(
            Permission.class,
            p -> p.getPermission().toLowerCase());

    Permission(String permission, boolean defaultPerm) {
        this.permission = "cascade." + permission;
        this.defaultPerm = defaultPerm;
    }

    Permission(String permission, boolean defaultPerm, CommandType commandType) {
        this.permission = "cascade." + permission;
        this.defaultPerm = defaultPerm;
        this.commandType = commandType;
    }

    Permission(String permission, boolean defaultPerm, net.dv8tion.jda.core.Permission... discordPerm) {
        this.permission = "cascade." + permission;
        this.defaultPerm = defaultPerm;
        this.discordPerm = EnumSet.noneOf(net.dv8tion.jda.core.Permission.class);
        this.discordPerm.addAll(Arrays.asList(discordPerm));
    }

    Permission(String permission) {
        this.permission = permission;
        this.defaultPerm = false;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isDefaultPerm() {
        return defaultPerm;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public static Permission getPermission(CommandType commandType) {
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
                    if (node.test(perm.getPermission())) return true;
                }
            }
        }
        return getPermission(permission.substring(permission.startsWith("-") ? 1 : 0)) != null;
    }

    public EnumSet<net.dv8tion.jda.core.Permission> getDiscordPerm() {
        return discordPerm;
    }

    @Override
    public String toString() {
        return getPermission();
    }

}
