/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.objects.pagination.Page;
import com.cascadebot.cascadebot.objects.pagination.PageObjects;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInfoCommand implements ICommand {
    @Override
    public void onCommand(Member sender, CommandContext context) {
        Member info = sender;
        if(context.getArgs().length > 0) {
            info = DiscordUtils.getMember(context.getMessage(0), context.getGuild());
        }
        if(info == null) {
            context.reply("Invalid User!");
            return;
        }
        User user = info.getUser();

        List<Page> pageList = new ArrayList<>();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(DiscordUtils.getTag(user));
        builder.setThumbnail(user.getAvatarUrl());
        builder.addField("Crated Data", "TODO", true);
        builder.addField("Join data", "TODO", true);

        pageList.add(new PageObjects.EmbedPage(builder));

        List<String> header = Arrays.asList("Role Id", "Role Name");

        List<List<String>> body = new ArrayList<>();
        for(Role role : info.getRoles()) {
            List<String> row = new ArrayList<>();
            row.add(role.getId());
            row.add(role.getName());
            body.add(row);
        }

        pageList.add(new PageObjects.TablePage(header, body));

        context.sendPagedMessage(pageList);
    }

    @Override
    public String defaultCommand() {
        return "userinfo";
    }

    @Override
    public CommandType getType() {
        return null;
    }
}
