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
import com.cascadebot.cascadebot.utils.pagination.Page;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServerInfoCommand implements ICommand {
    @Override
    public void onCommand(Member sender, CommandContext context) {
        List<Page> pageList = new ArrayList<>();

        EmbedBuilder builder = MessagingObjects.getInfoEmbedBuilder();
        builder.setTitle(context.getGuild().getName());
        builder.setThumbnail(context.getGuild().getIconUrl());
        builder.addField("Creation Date", FormatUtils.formatDateTime(context.getGuild().getCreationTime()), true);
        builder.addField("Guild Name", context.getGuild().getName(), true);
        builder.addField("Owner", context.getGuild().getOwner().toString(), true);
        builder.addField("Region", context.getGuild().getRegion().toString(), true);
        builder.addField("Guild ID", context.getGuild().getId(), true);
        builder.addField("Member Count", context.getGuild().getMembers().size() + "\n", true);

        context.sendPagedMessage(pageList);
    }

    @Override
    public String defaultCommand() {
        return "serverinfo";
    }

    @Override
    public CommandType getType() {
        return CommandType.CORE;
    }
}
