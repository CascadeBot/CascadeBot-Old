package com.cascadebot.cascadebot.commands;

import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class CommandContext {

    private TextChannel channel;
    private GuildData data;
    private String[] args;
    private Member member;
    private String originalCommand;

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

    public String getMessage(int start) {
        return getMessage(start, args.length);
    }

    public String getMessage(int start, int end) {
        return String.join(" ", ArrayUtils.subarray(args, start, end));
    }

    public boolean isArgInteger(int index) {
        return Constants.INTEGER_REGEX.matcher(this.args[index]).matches();
    }

    public boolean isArgDecimal(int index) {
        return Constants.DECIMAL_REGEX.matcher(this.args[index]).matches();
    }

    public String getArg(int index) {
        return this.args[index];
    }

    public int getArgAsInteger(int index) {
        return Integer.parseInt(this.args[index]);
    }

    public Double getArgAsDouble(int index) {
        return Double.parseDouble(StringUtils.replace(this.args[index], ",", "."));
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

    public User getUser() {
        return member.getUser();
    }

    public void setOriginalCommand(String originalCommand) {
        this.originalCommand = originalCommand;
    }

    public String getOriginalCommand() {
        return originalCommand;
    }

}
