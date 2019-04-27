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
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class GuildSaveSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            GuildDataManager.replace(context.getGuild().getIdLong(), context.getData());
            context.getTypedMessaging().replySuccess(context.i18n("commands.guild.save.saved_guild_successfully"));
        } else if (context.getArg(0).equals("all")) {
            GuildDataManager.getGuilds().asMap().forEach(GuildDataManager::replace);
            context.getTypedMessaging().replySuccess(context.i18n("commands.guild.save.saved_all_successfully"));
        } else {
            GuildData guildData = GuildDataManager.getGuilds().asMap().get(Long.parseLong(context.getArg(0)));
            if (guildData == null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.guild.save.cannot_save"));
                return;
            }
            GuildDataManager.replace(guildData.getGuildID(), guildData);
            context.getTypedMessaging().replySuccess(context.i18n("commands.guild.save.saved_other_successfully", context.getArg(0)));
        }
    }

    @Override
    public String command() {
        return "save";
    }

    @Override
    public String parent() {
        return "guild";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.ofA("guild_id", "Saves a specific guild", ArgumentType.REQUIRED, Set.of("all")));
    }


}
