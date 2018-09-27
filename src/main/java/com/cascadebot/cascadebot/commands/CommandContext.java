package com.cascadebot.cascadebot.commands;

import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.entities.TextChannel;

public class CommandContext {

    private final TextChannel channel;
    private final  GuildData data;

    public CommandContext(TextChannel channel, GuildData data, String[] args) {
        this.channel = channel;
        this.data = data;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public GuildData getData() {
        return data;
    }
}
