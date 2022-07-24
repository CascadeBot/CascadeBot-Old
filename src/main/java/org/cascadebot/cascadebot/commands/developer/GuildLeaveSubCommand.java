/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;

public class GuildLeaveSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        // TODO: Leave other guilds
        Messaging.sendMessage(MessageType.SUCCESS, context.getChannel(), "Goodbye!")
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

}
