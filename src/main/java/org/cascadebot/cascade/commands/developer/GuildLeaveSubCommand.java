/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.developer;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandExecutable;
import org.cascadebot.cascade.messaging.Messaging;
import org.cascadebot.cascade.permissions.CascadePermission;

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
