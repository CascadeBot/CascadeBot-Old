/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.informational;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.IMainCommand;
import com.cascadebot.cascadebot.permissions.Permission;
import com.cascadebot.cascadebot.utils.FormatUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RolesCommand implements IMainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Guild guildForRole = context.getGuild();

        if(context.getArgs().length > 0) {
            guildForRole = CascadeBot.INS.getShardManager().getGuildById(context.getArg(0));
        }
        if(guildForRole == null) {
            context.replyDanger("We couldn't find that guild!");
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        List<String> header = Arrays.asList("Role ID", "Role Name");

        List<List<String>> body = new ArrayList<>();
        for(Role role : guildForRole.getRoles()) {
            List<String> row = new ArrayList<>();
            row.add(role.getId());
            row.add(role.getName());
            body.add(row);
        }

        context.reply(FormatUtils.makeAsciiTable(header, body, null));
    }

    @Override
    public String command() {
        return "roles";
    }

    @Override
    public CommandType getType() {
        return CommandType.INFORMATIONAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.ROLES_COMMAND;
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("roleinfo");
    }
}