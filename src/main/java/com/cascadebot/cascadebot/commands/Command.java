package com.cascadebot.cascadebot.commands;

import com.cascadebot.cascadebot.Config;
import net.dv8tion.jda.core.entities.Member;

public interface Command {

    public void onCommand(Member sender, CommandContext context);

    public String defaultCommand();

    public CommandType getType();

    default boolean forceDefault() {
        return false;
    }

    default CommandLevel getCommandLevel() {
        return CommandLevel.USER;
    }

    public enum CommandLevel {
        USER,
        STAFF,
        DEVELOPER,
        OWNER;

        public static boolean contains(String test) {

            for (CommandLevel c : CommandLevel.values()) {
                if (c.name().equals(test)) {
                    return true;
                }
            }

            return false;
        }

        public long getId() {
            return Config.VALUES.commandLevels.getOrDefault(this, -1L);
        }

    }

}
