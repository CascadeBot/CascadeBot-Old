/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.exceptions;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.exceptions.PermissionException;

// We need this because the permission constructor is protected :(
public class DiscordPermissionException extends PermissionException {

    public DiscordPermissionException(String reason) {
        super(reason);
    }

    public DiscordPermissionException(Permission permission) {
        super(permission);
    }

    public DiscordPermissionException(Permission permission, String reason) {
        super(permission, reason);
    }

}
