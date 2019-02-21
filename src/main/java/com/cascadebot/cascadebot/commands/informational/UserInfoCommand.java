/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.informational;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import com.cascadebot.cascadebot.utils.Table;
import com.cascadebot.cascadebot.utils.pagination.Page;
import com.cascadebot.cascadebot.utils.pagination.PageObjects;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserInfoCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Member memberForInfo = sender;
        if (context.getArgs().length > 0) {
            memberForInfo = DiscordUtils.getMember(context.getMessage(0), context.getGuild());
        }
        if (memberForInfo == null) {
            context.reply("Invalid User!");
            return;
        }
        User user = memberForInfo.getUser();

        String status = "";

        if (memberForInfo.getOnlineStatus() == OnlineStatus.ONLINE) {
            status = context.globalEmote("online");
        } else if (memberForInfo.getOnlineStatus() == OnlineStatus.OFFLINE) {
            status = context.globalEmote("offline");
        } else if (memberForInfo.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB) {
            status = context.globalEmote("dnd");
        } else if (memberForInfo.getOnlineStatus() == OnlineStatus.IDLE) {
            status = context.globalEmote("idle");
        }

        List<Page> pageList = new ArrayList<>();
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(user.getAsTag());
        builder.setThumbnail(user.getAvatarUrl());
        builder.addField("User Created", user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
        builder.addField("Join Date", memberForInfo.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
        builder.addField("User ID", user.getId(), true);
        builder.addField("Status", StringUtils.capitalize(memberForInfo.getOnlineStatus().toString().replace("_", " ").toLowerCase()) + "  " + status, true);

        Game game = memberForInfo.getGame();
        if (game != null && !game.isRich()) {
            String gameStatus = "";
            if (game.isRich()) {
                // TODO: This will require API I think
            } else {
                gameStatus = StringUtils.capitalize(game.getType().toString().toLowerCase()) + " " + game.getName();
            }
            builder.addField("Activity", gameStatus, true);
        }
        pageList.add(new PageObjects.EmbedPage(builder));

        Table.TableBuilder tableBuilder = new Table.TableBuilder("Role ID", "Role Name");

        for (Role role : memberForInfo.getRoles()) {
            tableBuilder.addRow(role.getId(), role.getName());
        }

        pageList.add(new PageObjects.TablePage(tableBuilder.build()));

        context.sendPagedMessage(pageList);
    }

    @Override
    public String command() {
        return "userinfo";
    }

    @Override
    public Module getModule() {
        return Module.INFORMATIONAL;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("User info command", "userinfo", true);
    }

}
