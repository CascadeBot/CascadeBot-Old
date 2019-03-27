/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.subcommands.guild;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class GuildSaveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            GuildDataManager.replace(context.getGuild().getIdLong(), context.getData());
            context.replySuccess("Saved **this guild's** information successfully!");
        } else if (context.getArg(0).equals("all")) {
            GuildDataManager.getGuilds().asMap().forEach(GuildDataManager::replace);
            context.replySuccess("Saved **all** guild information successfully!");
        } else {
            GuildData guildData = GuildDataManager.getGuilds().asMap().get(Long.parseLong(context.getArg(0)));
            if (guildData == null) {
                context.replyDanger("Cannot find guild to save!");
                return;
            }
            GuildDataManager.replace(guildData.getGuildID(), guildData);
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
        return Set.of(Argument.ofA("guild_id", "Saves a specific guild", ArgumentType.REQUIRED, Set.of("all")));
    }


}
