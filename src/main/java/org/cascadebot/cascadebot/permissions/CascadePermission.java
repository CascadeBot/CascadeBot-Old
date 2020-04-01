/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.permissions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

import java.util.Arrays;
import java.util.EnumSet;

@EqualsAndHashCode
@Getter
public class CascadePermission {

    public static final CascadePermission ALL_PERMISSIONS = CascadePermission.of("*", false);

    private final String permissionRaw;
    private final boolean defaultPerm;
    private final EnumSet<Permission> discordPerm;
    private final Module module;


    private CascadePermission(String permissionRaw, boolean defaultPerm, Module module, Permission... discordPerm) {
        this.permissionRaw = permissionRaw;
        this.defaultPerm = defaultPerm;
        this.module = module;
        this.discordPerm = EnumSet.noneOf(Permission.class);
        this.discordPerm.addAll(Arrays.asList(discordPerm));
    }

    public static CascadePermission of(String permission, boolean defaultPerm) {
        return new CascadePermission(permission, defaultPerm, null);
    }

    public static CascadePermission of(String permission, boolean defaultPerm, Module module) {
        return new CascadePermission(permission, defaultPerm, module);
    }

    public static CascadePermission of(String permission, boolean defaultPerm, Permission... discordPerm) {
        return new CascadePermission(permission, defaultPerm, null, discordPerm);
    }

    public static CascadePermission of(String permission, boolean defaultPerm, Module module, Permission... discordPerm) {
        return new CascadePermission(permission, defaultPerm, module, discordPerm);
    }

    public String getPermissionRaw() {
        return permissionRaw;
    }

    public String getPermission(Locale locale) {
        return Language.i18n(locale, "permissions." + permissionRaw + ".name");
    }

    public String getLabel(Locale locale) {
        return Language.i18n(locale, "permissions." + permissionRaw + ".label");
    }

    public EnumSet<Permission> getDiscordPerms() {
        return discordPerm;
    }

    @Override
    public String toString() {
        return getPermissionRaw();
    }

}
