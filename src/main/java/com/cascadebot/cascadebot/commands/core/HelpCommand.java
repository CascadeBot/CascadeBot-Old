/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import com.cascadebot.cascadebot.utils.StringsUtil;
import com.cascadebot.cascadebot.utils.pagination.Page;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements ICommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        List<Page> pageList = new ArrayList<>();
        EmbedBuilder builder = MessagingObjects.getInfoEmbedBuilder();
        builder.setTitle("CascadeBot Help");
        builder.setThumbnail(CascadeBot.INS.getSelfUser().getAvatarUrl());
        builder.addField("Commands", "TODO", true);
    }

    @Override
    public String defaultCommand() {
        return "help";
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

    @Override
    public CommandType getType() {
        return CommandType.CORE;
    }

}
