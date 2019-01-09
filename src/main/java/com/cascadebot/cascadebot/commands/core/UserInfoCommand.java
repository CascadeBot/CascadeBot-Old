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
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInfoCommand implements ICommand {
    @Override
    public void onCommand(Member sender, CommandContext context) {
        Member memberForInfo = sender;
        if(context.getArgs().length > 0) {
            memberForInfo = DiscordUtils.getMember(context.getMessage(0), context.getGuild());
        }
        if(memberForInfo == null) {
            context.reply("Invalid User!");
            return;
        }
        User user = memberForInfo.getUser();

        List<Page> pageList = new ArrayList<>();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(DiscordUtils.getTag(user));
        builder.setThumbnail(user.getAvatarUrl());
        builder.addField("Crated Data", "TODO", true);
        builder.addField("Join Data", "TODO", true);
        builder.addField("User ID", context.getUser().getId() + "", true);
        builder.addField("Name + Tag", context.getUser().getName() + context.getUser().getDiscriminator(), true);
        builder.addField("Avatar Link", context.getUser().getAvatarUrl() + "", true);
        builder.addField("Status", context.getMember().getOnlineStatus() + "", true);
        builder.addField("Currently Playing", context.getMember().getGame() + "", true);
        pageList.add(new PageObjects.EmbedPage(builder));

        List<String> header = Arrays.asList("Role ID", "Role Name");

        List<List<String>> body = new ArrayList<>();
        for(Role role : memberForInfo.getRoles()) {
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
