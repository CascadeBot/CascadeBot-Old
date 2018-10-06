/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class CommandContext {

    private final TextChannel channel;
    private final Message message;
    private final Guild guild;
    private final GuildData data;

    private final String[] args;
    private final Member invoker;
    private final String trigger;
    private final boolean isMention;

    public CommandContext(TextChannel channel, Message message, Guild guild, GuildData data, String[] args, Member invoker,
                          String trigger, boolean isMention) {
        this.channel = channel;
        this.message = message;
        this.guild = guild;
        this.data = data;
        this.args = args;
        this.invoker = invoker;
        this.trigger = trigger;
        this.isMention = isMention;
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

    public Member getMember() {
        return invoker;
    }

    public User getUser() {
        return invoker.getUser();
    }

    public String getTrigger() {
        return trigger;
    }

}
