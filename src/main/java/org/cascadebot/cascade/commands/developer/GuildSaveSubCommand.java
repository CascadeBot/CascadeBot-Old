/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.developer;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.Argument;
import org.cascadebot.cascade.commandmeta.ArgumentType;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandExecutable;
import org.cascadebot.cascade.data.managers.GuildDataManager;
import org.cascadebot.cascade.data.objects.GuildData;
import org.cascadebot.cascade.permissions.CascadePermission;

import java.util.Set;

public class GuildSaveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            GuildDataManager.replace(context.getGuild().getIdLong(), context.getData());
            context.getTypedMessaging().replySuccess("Saved **this guild's** information successfully!");
        } else if (context.getArg(0).equals("all")) {
            GuildDataManager.getGuilds().asMap().forEach(GuildDataManager::replace);
            context.getTypedMessaging().replySuccess("Saved **all** guild information successfully!");
        } else {
            GuildData guildData = GuildDataManager.getGuilds().asMap().get(Long.parseLong(context.getArg(0)));
            if (guildData == null) {
                context.getTypedMessaging().replyDanger("Cannot find guild to save!");
                return;
            }
            GuildDataManager.replace(guildData.getGuildID(), guildData);
            context.getTypedMessaging().replySuccess("Saved guild information for guild **" + context.getArg(0) + "**!");
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
