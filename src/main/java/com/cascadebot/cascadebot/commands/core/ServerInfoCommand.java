/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import com.cascadebot.cascadebot.utils.FormatUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Guild.*;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.Set;

import static io.netty.util.internal.SystemPropertyUtil.contains;

public class ServerInfoCommand implements ICommand {
    @Override
    public void onCommand(Member sender, CommandContext context) {
        Guild guildForInfo = context.getGuild();
        Message userMessage = context.getMessage();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(guildForInfo.getName());
        builder.setThumbnail(guildForInfo.getIconUrl());
        builder.setDescription("Guild Information");
        builder.addField("Creation Date", FormatUtils.formatDateTime(guildForInfo.getCreationTime()), true);
        builder.addField("Guild Name", guildForInfo.getName(), true);
        builder.addField("Owner", guildForInfo.getOwner().getUser().getAsTag(), true);
        builder.addField("Region", guildForInfo.getRegion().toString(), true);
        builder.addField("Member Count", String.valueOf(guildForInfo.getMembers().size()), true);
        builder.setFooter("ID: " + guildForInfo.getId(), guildForInfo.getIconUrl());

        context.replyInfo(builder);
    }

    @Override
    public String defaultCommand() {
        return "serverinfo";
    }

    @Override
    public CommandType getType() {
        return CommandType.CORE;
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("guildinfo");
    }
}
