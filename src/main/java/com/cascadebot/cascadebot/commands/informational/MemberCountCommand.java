/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.informational;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import com.cascadebot.cascadebot.permissions.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;

public class MemberCountCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {

        Guild guildForMembers = context.getGuild();

        if (context.getArgs().length > 0) {
            guildForMembers = CascadeBot.INS.getShardManager().getGuildById(context.getArg(0));
        }
        if (guildForMembers == null) {
            context.replyDanger("We can't seem to find that guild!");
            return;
        }

        String memberCount = String.valueOf(guildForMembers.getMembers().size());

        context.reply("Members: " + memberCount);
    }

    @Override
    public String command() {
        return "membercount";
    }

    @Override
    public Permission getPermission() {
        return Permission.of("Member count command", "membercount", true);
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("members");
    }

}
