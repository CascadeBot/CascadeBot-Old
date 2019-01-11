/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import com.cascadebot.cascadebot.utils.pagination.Page;
import com.cascadebot.cascadebot.utils.pagination.PageObjects;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerInfoCommand implements ICommand {
    @Override
    public void onCommand(Member sender, CommandContext context) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(context.getGuild().getName());
        builder.setThumbnail(context.getGuild().getIconUrl());
        builder.addField("Creation Date", context.getGuild().getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
        builder.addField("Guild Name", context.getGuild().getName(), true);
        builder.addField("Owner", context.getGuild().getOwner().toString(), true);
        builder.addField("Region", context.getGuild().getRegion().toString(), true);
        builder.addField("Guild ID", context.getGuild().getId(), true);
        builder.addField("Member Count", context.getGuild().getMembers().size() + "\n", true);
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
