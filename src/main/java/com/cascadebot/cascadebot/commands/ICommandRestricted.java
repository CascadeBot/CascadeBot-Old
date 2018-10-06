package com.cascadebot.cascadebot.commands;

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
            return Config.VALUES.commandLevels.getOrDefault(this, -1L);
        }

    }

}
