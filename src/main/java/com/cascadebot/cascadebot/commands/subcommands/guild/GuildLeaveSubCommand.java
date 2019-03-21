/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.subcommands.guild;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.messaging.Messaging;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.entities.Member;

public class GuildLeaveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Messaging.sendSuccessMessage(context.getChannel(), "Goodbye!")
                .thenAccept(message -> context.getGuild().leave().queue());
    }

    @Override
    public String command() {
        return "leave";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

    @Override
    public String description() {
        return "Instructs the bot to leave the current guild";
    }

}
