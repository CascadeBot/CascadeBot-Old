/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import org.cascadebot.cascadebot.commandmeta.Module;

/**
 * Represents a data object that contains a per guild permission
 */
public interface PermissionObject {
    String getPermission();

    String getParent();

    Module cascadeModule();

    default GuildPermission getInternalPermission() {
        return new GuildPermission(getParent() + "." + getPermission(), false, cascadeModule());
    }
}
