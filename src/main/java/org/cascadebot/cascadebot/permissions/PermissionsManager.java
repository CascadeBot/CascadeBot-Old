/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.permissions;

import com.google.common.collect.ImmutableSet;
import io.github.binaryoverload.JSONConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.DeprecatedExecutableCommand;
import org.cascadebot.cascadebot.commandmeta.ExecutableCommand;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.RestrictedCommand;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.shared.SecurityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PermissionsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionsManager.class);

    // <Localised, English>
    private HashMap<String, String> localisedPermissionsMapping = new HashMap<>();

    private HashMap<String, CascadePermission> permissions = new HashMap<>();
    private Set<CascadePermission> defaultPermissions = Set.of();

    public PermissionsManager() {
        for (Locale locale : Language.getLanguages().keySet()) {
            if (locale == Locale.getDefaultLocale()) {
                continue;
            }
            JSONConfig config = Language.getLanguages().get(locale);
            if (config.getElement("permissions").isEmpty()) {
                continue;
            }
            for (String permission : permissions.keySet()) {
                if (config.getString("permissions." + permission + ".name").isPresent()) {
                    localisedPermissionsMapping.put(config.getString("permissions." + permission + ".name").get(), permission);
                }
            }
        }
    }

    public void registerPermissions() {
        if (!permissions.isEmpty()) {
            throw new IllegalStateException("Permissions have already been registered!");
        }

        long startTime = System.currentTimeMillis();

        for (MainCommand command : CascadeBot.INS.getCommandManager().getCommands()) {
            if (command.permission() == null || command instanceof RestrictedCommand) {
                continue;
            }
            registerPermission(command.permission());
            for (DeprecatedExecutableCommand subCommand : command.subCommands()) {
                if (subCommand.permission() == null) {
                    continue;
                }
                registerPermission(subCommand.permission());
            }
        }

        registerPermission(CascadePermission.ALL_PERMISSIONS);
        registerPermission(CascadePermission.of("module.info", false, Module.INFORMATIONAL));
        registerPermission(CascadePermission.of("module.fun", false, Module.FUN));
        registerPermission(CascadePermission.of("module.music", false, Module.MUSIC));
        registerPermission(CascadePermission.of("module.moderation", false, Module.MODERATION));
        registerPermission(CascadePermission.of("module.management", false, Module.MANAGEMENT));

        registerPermission(CascadePermission.of("prefix.reset", false, Permission.MANAGE_SERVER));
        registerPermission(CascadePermission.of("prefix.set", false, Permission.MANAGE_SERVER));

        registerPermission(CascadePermission.of("join.other", false, Permission.VOICE_MOVE_OTHERS));
        registerPermission(CascadePermission.of("leave.other", false, Permission.VOICE_MOVE_OTHERS));

        registerPermission(CascadePermission.of("volume.extreme", false, Permission.MANAGE_SERVER));
        registerPermission(CascadePermission.of("skip.force", false, Permission.MANAGE_CHANNEL));
        registerPermission(CascadePermission.of("queue.save.overwrite", false));

        registerPermission(CascadePermission.of("filters.bypass", false));

        LOGGER.info("{} permissions loaded in {}ms!", permissions.size(), System.currentTimeMillis() - startTime);

        defaultPermissions = permissions.values()
                .stream()
                .filter(CascadePermission::isDefaultPerm)
                .collect(ImmutableSet.toImmutableSet());

    }

    private void registerPermission(CascadePermission permission) {
        permissions.put(permission.getPermissionRaw(), permission);
    }

    public CascadePermission getPermission(String permission) {
        return permissions.get(permission);
    }

    public CascadePermission getPermissionFromModule(Module module) {
        return permissions
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
                    if (node.test(perm.getPermissionRaw())) {
                        return true;
                    }
                }
            }
        }
        return getPermission(permission.substring(permission.startsWith("-") ? 1 : 0)) != null;
    }

    public Set<CascadePermission> getDefaultPermissions() {
        return defaultPermissions;
    }

    public Map<String, String> getLocalisedPermissionMapping() {
        return Map.copyOf(localisedPermissionsMapping);
    }

    public Set<CascadePermission> getPermissions() {
        return getPermissions(false);
    }

    public Set<CascadePermission> getPermissions(boolean defaultOnly) {
        if (defaultOnly) {
            return defaultPermissions;
        } else {
            return permissions.values().stream().collect(ImmutableSet.toImmutableSet());
        }
    }

    public boolean isAuthorised(ExecutableCommand command, GuildData guildData, Member member) {
        /*if (command instanceof RestrictedCommand) {
            SecurityLevel userLevel = getUserSecurityLevel(member.getIdLong());
            if (userLevel == null) {
                return false;
            }
            SecurityLevel levelToCheck = ((RestrictedCommand) command).commandLevel();
            return userLevel.isAuthorised(levelToCheck);
        } else {*/
            if (command.getPermission() == null) {
                return true;
            }
            return guildData.getManagement().getPermissions().hasPermission(member, CascadePermission.of(command.getPermission(), false) /* TODO fix this */, guildData.getCore());
        //}
        // return false;
    }

    public SecurityLevel getUserSecurityLevel(long userId) {
        return Security.getLevelById(userId, DiscordUtils.getAllOfficialRoleIds(userId));
    }

    public Map<String, CascadePermission> getPermissionMap() {
        return Map.copyOf(permissions);
    }

}
