/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.informational;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import com.cascadebot.cascadebot.permissions.Permission;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import com.cascadebot.cascadebot.utils.pagination.Page;
import com.cascadebot.cascadebot.utils.pagination.PageObjects;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInfoCommand implements ICommandMain {
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
        EmbedBuilder builder = MessagingObjects.getInfoEmbedBuilder();
        builder.setTitle(user.getAsTag());
        builder.setThumbnail(user.getAvatarUrl());
        builder.addField("User Created", user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
        builder.addField("Join Date", memberForInfo.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
        builder.addField("User ID", user.getId(), true);
        builder.addField("Status", StringUtils.capitalize(memberForInfo.getOnlineStatus().toString().replace("_", " ").toLowerCase()), true);

        Game game = memberForInfo.getGame();
        if (game != null && !game.isRich()) {
            String status = "";
            if (game.isRich()) {
                // TODO: This will require API I think
            } else {
                status = StringUtils.capitalize(game.getType().toString().toLowerCase()) + " " + game.getName();
            }
            builder.addField("Activity", status, true);
        }
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
    public String command() {
        return "userinfo";
    }

    @Override
    public CommandType getType() {
        return CommandType.INFORMATIONAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.of("User info command", "userinfo", true);
    }

}
