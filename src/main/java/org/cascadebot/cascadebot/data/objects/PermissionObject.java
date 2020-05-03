/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

/**
 * Represents a data object that contains a per guild permission
 */
public abstract class PermissionObject {
    abstract String getPermission();

    abstract String getParent();

    abstract Module cascadeModule();

    public CascadePermission getInternalPermission() {
        return CascadePermission.of(getParent() + "." + getPermission(), false, cascadeModule());
    }
}
