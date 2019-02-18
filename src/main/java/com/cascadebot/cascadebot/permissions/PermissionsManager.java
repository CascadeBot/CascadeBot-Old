/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.permissions;

import com.cascadebot.cascadebot.commandmeta.CommandManager;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import com.cascadebot.shared.SecurityLevel;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PermissionsManager {

    public static final String PERMISSION_PREFIX = "cascade.";

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionsManager.class);

    private LoadingCache<Long, Set<Long>> officialGuildRoleIDCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(DiscordUtils::getAllOfficialRoleIds);
    private LoadingCache<Long, SecurityLevel> securityLevelCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(id -> Security.getLevelById(id, officialGuildRoleIDCache.get(id)));

    private ConcurrentHashMap<String, CascadePermission> permissions = new ConcurrentHashMap<>();
    private Set<CascadePermission> defaultPermissions = Set.of();

    public void registerPermissions() {
        if (!permissions.isEmpty()) throw new IllegalStateException("Permissions have already been registered!");

        long startTime = System.currentTimeMillis();

        for (ICommandMain command : CommandManager.instance().getCommands()) {
            if (command.getPermission() == null || command instanceof ICommandRestricted) continue;
            registerPermission(command.getPermission());
            for (ICommandExecutable subCommand : command.getSubCommands()) {
                registerPermission(subCommand.getPermission());
            }
        }

        registerPermission(CascadePermission.of("Core Category", "module.core", true, Module.CORE));
        registerPermission(CascadePermission.of("Info Category", "module.info", true, Module.INFORMATIONAL));
        registerPermission(CascadePermission.of("Fun Category", "module.fun", true, Module.FUN));

        registerPermission(CascadePermission.of("Reset command prefix", "prefix.reset", false, Permission.MANAGE_SERVER));
        registerPermission(CascadePermission.of("Set command prefix", "prefix.set", false, Permission.MANAGE_SERVER));


        LOGGER.info("{} permissions loaded in {}ms!", permissions.size(), System.currentTimeMillis() - startTime);

        defaultPermissions = permissions.entrySet()
                .stream()
                .filter(p -> p.getValue().isDefaultPerm())
                .map(Map.Entry::getValue)
                .collect(ImmutableSet.toImmutableSet());

    }

    private void registerPermission(CascadePermission permission) {
        permissions.put(permission.getPermissionNode(), permission);
    }

    public CascadePermission getPermission(String permission) {
        return permissions.get(PERMISSION_PREFIX + permission);
    }

    public CascadePermission getPermissionFromModule(Module module) {
        return permissions
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getModule().equals(module))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null); // Gets the permission connected to this module or returns null
    }

    public boolean isValidPermission(String permission) {
        return isValidPermission(null, permission);
    }

    public boolean isValidPermission(Guild guild, String permission) {
        Set<CascadePermission> permissions = getPermissions(guild);
        if (permission.contains("*") && permission.contains(".")) {
            PermissionNode node = new PermissionNode(permission);
            for (CascadePermission perm : permissions) {
                if (perm != CascadePermission.ALL_PERMISSIONS) {
                    if (node.test(perm.getPermissionNode())) return true;
                }
            }
        }
        return getPermission(permission.substring(permission.startsWith("-") ? 1 : 0)) != null;
    }

    public Set<CascadePermission> getDefaultPermissions() {
        return defaultPermissions;
    }

    public Set<CascadePermission> getPermissions() {
        return getPermissions(null);
    }

    public Set<CascadePermission> getPermissions(Guild guild) {
        return getPermissions(guild, false);
    }

    public Set<CascadePermission> getPermissions(Guild guild, boolean defaultOnly) {
        // TODO: Add custom permission to guild data and add it here
        if (defaultOnly) {
            return defaultPermissions;
        } else {
            return permissions.values().stream().collect(ImmutableSet.toImmutableSet());
        }
    }


    public boolean isAuthorised(ICommandExecutable command, GuildData guildData, Member member) {
        if (command instanceof ICommandRestricted) {
            SecurityLevel userLevel = securityLevelCache.get(member.getUser().getIdLong());
            if (userLevel == null) return false;
            SecurityLevel levelToCheck = ((ICommandRestricted) command).getCommandLevel();
            return userLevel.isAuthorised(levelToCheck);
        } else {
            // TODO: Checking command specific perms
            return true;
        }
        // return false;
    }


}
