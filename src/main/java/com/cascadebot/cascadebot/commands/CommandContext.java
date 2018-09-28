package com.cascadebot.cascadebot.commands;

import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandContext {

    private final TextChannel channel;
    private final  GuildData data;
    private String[] args;

    public CommandContext(TextChannel channel, GuildData data, String[] args) {
        this.channel = channel;
        this.data = data;
        this.args = args;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public GuildData getData() {
        return data;
    }

    public String[] getArgs() {
        return args;
    }

    public String getMessageFromArgs(int start) {
        return getMessageFromArgs(start, args.length);
    }

    public String getMessageFromArgs(int start, int end) {
        List<String> messageParts = Arrays.asList(Arrays.copyOfRange(args, start, end));
        return messageParts.stream().collect(Collectors.joining(" "));
    }
}
