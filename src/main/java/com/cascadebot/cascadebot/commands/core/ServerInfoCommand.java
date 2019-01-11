/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import com.cascadebot.cascadebot.utils.FormatUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import java.util.Set;

public class ServerInfoCommand implements ICommand {
    @Override
    public void onCommand(Member sender, CommandContext context) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(context.getGuild().getName());
        builder.setThumbnail(context.getGuild().getIconUrl());
        builder.setDescription("Guild Information");
        builder.addField("Creation Date", FormatUtils.formatDateTime(context.getGuild().getCreationTime()), true);
        builder.addField("Guild Name", context.getGuild().getName(), true);
        builder.addField("Owner", context.getGuild().getOwner().getUser().getAsTag(), true);
        builder.addField("Region", context.getGuild().getRegion().toString(), true);
        builder.addField("Member Count", String.valueOf(context.getGuild().getMembers().size()), true);
        builder.setFooter("ID: " + context.getGuild().getId(), context.getGuild().getIconUrl());

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
