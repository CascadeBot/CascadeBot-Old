/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.informational;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.commands.developer.EvalCommand;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.events.CommandListener;
import com.cascadebot.cascadebot.tasks.Task;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import com.cascadebot.cascadebot.utils.FormatUtils;
import com.cascadebot.cascadebot.utils.pagination.PageObjects;
import com.cascadebot.shared.ExitCodes;
import com.cascadebot.shared.SharedConstants;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RoleCommand implements ICommand {
    @Override
    public void onCommand(Member sender, CommandContext context) {

        EmbedBuilder builder = new EmbedBuilder();
        List<String> header = Arrays.asList("Role ID", "Role Name");

        List<List<String>> body = new ArrayList<>();
        for(Role role : context.getGuild().getRoles()) {
            List<String> row = new ArrayList<>();
            row.add(role.getId());
            row.add(role.getName());
            body.add(row);
        }
        context.replyInfo(builder);
    }

    @Override
    public String defaultCommand() {
        return "roles";
    }

    @Override
    public CommandType getType() {
        return CommandType.INFORMATIONAL;
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("roleinfo");
    }
}