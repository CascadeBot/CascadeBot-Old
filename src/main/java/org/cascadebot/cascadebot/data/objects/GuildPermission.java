/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import net.dv8tion.jda.api.Permission;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class GuildPermission extends CascadePermission {

    protected GuildPermission(String permissionRaw, boolean defaultPerm, Module module, Permission... discordPerm) {
        super(permissionRaw, defaultPerm, module, discordPerm);

    }

}
