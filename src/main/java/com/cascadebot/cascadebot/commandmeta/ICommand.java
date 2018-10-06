package com.cascadebot.cascadebot.commandmeta;

import net.dv8tion.jda.core.entities.Member;

public interface ICommand {

    public void onCommand(Member sender, CommandContext context);

    public String defaultCommand();

    public CommandType getType();

    default boolean forceDefault() {
        return false;
    }

    default String[] getGlobalAliases() {
        return new String[0];
    }

}
