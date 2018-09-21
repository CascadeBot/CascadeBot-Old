package com.cascadebot.cascadebot.commands;

import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

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
        DEVELOPER (1203034432),
        ADMIN (10293949),
        OWNER (1000000);

        long l;

        CommandLevel(long l) {
            this.l = l;
        }
    }

}
