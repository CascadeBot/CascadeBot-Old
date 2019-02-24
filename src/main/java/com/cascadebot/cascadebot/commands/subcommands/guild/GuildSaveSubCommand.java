/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.subcommands.guild;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.events.GuildSaveListener;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.entities.Member;

public class GuildSaveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            GuildDataMapper.replace(context.getGuild().getIdLong(), context.getData());
            context.replySuccess("Saved **this guild's** information successfully!");
        } else if (context.getArg(0).equals("all")) {
            GuildDataMapper.getGuilds().asMap().forEach(GuildDataMapper::replace);
            context.replySuccess("Saved **all** guild information successfully!");
        } else {
            GuildData guildData = GuildDataMapper.getGuilds().asMap().get(Long.parseLong(context.getArg(0)));
            if (guildData == null) {
                context.replyDanger("Cannot find guild to save!");
                return;
            }
            GuildDataMapper.replace(guildData.getGuildID(), guildData);
            context.replySuccess("Saved guild information for guild **" + context.getArg(0) + "**!");
        }
    }

    @Override
    public String command() {
        return "save";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

}
