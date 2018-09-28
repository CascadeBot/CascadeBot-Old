package com.cascadebot.cascadebot.commands;

import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandContext {

    private TextChannel channel;
    private GuildData data;
    private String[] args;
    private Member member;

    public CommandContext() {
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

    public void setChannel(TextChannel channel) {
        this.channel = channel;
    }

    public void setGuildData(GuildData data) {
        this.data = data;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

}
