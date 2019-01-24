/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.informational;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.permissions.Permission;
import com.cascadebot.cascadebot.utils.FormatUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;

public class ServerInfoCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Guild guildForInfo = context.getGuild();

        if(context.getArgs().length > 0) {
            guildForInfo = CascadeBot.INS.getShardManager().getGuildById(context.getArg(0));
        }
        if(guildForInfo == null) {
            context.replyDanger("We couldn't find that guild!");
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(guildForInfo.getName());
        builder.setThumbnail(guildForInfo.getIconUrl());
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
        return CommandType.INFORMATIONAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.SERVER_INFO_COMMAND;
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("guildinfo");
    }
}
