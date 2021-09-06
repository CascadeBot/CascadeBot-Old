/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class GuildSaveSubCommand extends DeprecatedSubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            GuildDataManager.replace(context.getGuild().getIdLong(), context.getData());
            context.getTypedMessaging().replySuccess("Saved **this guild's** information successfully!");
        } else if (context.getArg(0).equals("all")) {
            GuildDataManager.getGuilds().asMap().forEach(GuildDataManager::replace);
            context.getTypedMessaging().replySuccess("Saved **all** guild information successfully!");
        } else {
            if (!context.isArgLong(0)) {
                context.getTypedMessaging().replyDanger("Please provide a valid ID!");
                return;
            }
            GuildData guildData = GuildDataManager.getGuilds().asMap().get(context.getArgAsLong(0));
            if (guildData == null) {
                context.getTypedMessaging().replyDanger("Cannot find a guild to save!");
                return;
            }
            GuildDataManager.replace(guildData.getGuildId(), guildData);
            context.getTypedMessaging().replySuccess("Saved guild information for guild **%s**!", context.getArg(0));
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
    public String description() {
        return "Save the current guild's data.";
    }

    @Override
    public CascadePermission permission() {
        return null;
    }

}
