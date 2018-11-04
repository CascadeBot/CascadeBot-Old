/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.Config;

public interface ICommandRestricted extends ICommand {

    default CommandLevel getCommandLevel() {
        return CommandLevel.STAFF;
    }

    enum CommandLevel {
        STAFF,
        DEVELOPER,
        OWNER;

        public long getId() {
            return Config.INS.getCommandLevels().getOrDefault(this, -1L);
        }

    }

}
