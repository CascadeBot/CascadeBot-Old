package com.cascadebot.cascadebot.commands;

import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public interface Command {
    public void onCommand(Member sender, GuildData data, TextChannel channel, String[] args);

    public String defaultCommand();

    default boolean forceDefault() {
        return false;
    }
}
