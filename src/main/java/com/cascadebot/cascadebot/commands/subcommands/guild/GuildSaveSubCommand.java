/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.subcommands.guild;

import com.cascadebot.cascadebot.commandmeta.Argument;
import com.cascadebot.cascadebot.commandmeta.ArgumentType;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;

public class GuildSaveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            GuildDataMapper.getGuilds().invalidate(context.getGuild().getIdLong());
            context.replySuccess("Saved **this guild's** information successfully!");
        } else if (context.getArg(0).equals("all")) {
            GuildDataMapper.getGuilds().invalidateAll();
            context.replySuccess("Saved **all** guild information successfully!");
        } else {
            GuildDataMapper.getGuilds().invalidate(context.getArg(0));
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

    @Override
    public String description() {
        return "save current guilds data";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.ofA("guildId", "Saves a specific guild", ArgumentType.REQUIRED, Set.of("all")));
    }


}
