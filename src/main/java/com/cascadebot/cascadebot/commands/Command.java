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



}
