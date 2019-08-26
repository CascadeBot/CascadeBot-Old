/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class GuildLeaveSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        // TODO: Leave other guilds
        Messaging.sendSuccessMessage(context.getChannel(), "Goodbye!")
                .thenAccept(message -> context.getGuild().leave().queue());
    }

    @Override
    public String command() {
        return "leave";
    }

    @Override
    public String parent() {
        return "guild";
    }

    @Override
    public String description() {
        return "Instructs the bot to leave the current guild.";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

}
