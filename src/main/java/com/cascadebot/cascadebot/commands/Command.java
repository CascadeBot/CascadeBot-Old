package com.cascadebot.cascadebot.commands;

import com.cascadebot.cascadebot.Config;
import net.dv8tion.jda.core.entities.User;

public interface Command {

    public void onCommand(User sender, CommandContext context);

    public String defaultCommand();

    public CommandType getType();

    default boolean forceDefault() {
        return false;
    }

    default CommandLevel getCommandLevel() {
        return CommandLevel.USER;
    }

    default String[] getGlobalAliases() {
        return new String[0];
    }

    public enum CommandLevel {
        USER,
        STAFF,
        DEVELOPER,
        OWNER;

        public long getId() {
            return Config.VALUES.commandLevels.getOrDefault(this, -1L);
        }

    }

}
