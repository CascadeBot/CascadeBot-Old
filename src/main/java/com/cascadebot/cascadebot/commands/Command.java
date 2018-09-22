package com.cascadebot.cascadebot.commands;

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
        USER (-1),
        STAFF (488433225711222804L), //TODO: Move these to config?
        DEVELOPER (488424540800548887L),
        OWNER (488430613049769996L);

        long roleID;

        CommandLevel(long roleID) {
            this.roleID = roleID;
        }
    }

}
