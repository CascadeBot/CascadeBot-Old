/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.ShutdownHandler;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.messaging.MessageType;
import com.cascadebot.cascadebot.permissions.SecurityLevel;
import com.cascadebot.cascadebot.tasks.Task;
import com.cascadebot.cascadebot.utils.ConfirmUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

public class ShutdownCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        // A confirmation check to make sure we actually want to shut down on production
        if (!CascadeBot.getVersion().getBuild().equalsIgnoreCase("dev") && !ConfirmUtils.hasConfirmedAction( "shutdown_bot", sender.getUser().getIdLong())) {
            ConfirmUtils.confirmAction(
                    sender.getUser().getIdLong(),
                    "shutdown_bot",
                    context.getChannel(),
                    MessageType.DANGER,
                    "It looks like the bot is running in production mode, **do you really want to do this?** \nIf so, simply repeat the command again. This confirmation will expire in one minute!",
                    new ConfirmUtils.ConfirmRunnable() {
                        @Override
                        public void execute() {
                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setFooter(sender.getUser().getAsTag(), sender.getUser().getEffectiveAvatarUrl());
                            builder.setDescription("Cascade bot shutting down!");
                            context.replyInfo(builder);
                            CascadeBot.logger.info("Shutting down via command! Issuer: " + context.getUser().getAsTag());
                            ShutdownHandler.stop();
                        }
                    });
            return;
        }
        ConfirmUtils.completeAction("shutdown_bot", sender.getUser().getIdLong());
    }

    @Override
    public String defaultCommand() {
        return "shutdown";
    }

    @Override
    public SecurityLevel getCommandLevel() { return SecurityLevel.OWNER; }

    @Override
    public CommandType getType() {
        return CommandType.DEVELOPER;
    }

    @Override
    public boolean forceDefault() {
        return true;
    }
}
