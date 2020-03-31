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
public interface PermissionObject {
    String getPermission();

    String getParent();

    Module cascadeModule();

    default CascadePermission getInternalPermission() {
        return new CascadePermission(getParent() + "." + getPermission(), false, cascadeModule());
    }
}
