/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.SecurityLevel;

public interface ICommandRestricted extends ICommand {

    default SecurityLevel getCommandLevel() {
        return SecurityLevel.STAFF;
    }

}
