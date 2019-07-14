/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.Environment;
import org.cascadebot.cascadebot.ShutdownHandler;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.utils.ConfirmUtils;
import org.cascadebot.shared.SecurityLevel;

public class ShutdownCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        // A confirmation check to make sure we actually want to shut down on production
        if (Environment.isProduction()) {
            if (!ConfirmUtils.hasConfirmedAction("shutdown_bot", sender.getIdLong())) {
                ConfirmUtils.confirmAction(
                        sender.getIdLong(),
                        "shutdown_bot",
                        context.getChannel(),
                        MessageType.DANGER,
                        "It looks like the bot is running in ***production*** mode, **do you _really_ want to do this?** \n If so, simply repeat the command again. This confirmation will expire in one minute!",
                        new ConfirmUtils.ConfirmRunnable() {
                            @Override
                            public void execute() {
                                shutdown(context);
                            }
                        });
                return;
            }
            ConfirmUtils.completeAction("shutdown_bot", sender.getIdLong());
        } else {
            shutdown(context);
        }
    }

    private void shutdown(CommandContext context) {
        if (ShutdownHandler.SHUTDOWN_LOCK.get()) {
            context.getTypedMessaging().replyDanger("The bot is already shutting down!");
            return;
        }

        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setFooter(context.getMember().getUser().getAsTag(), context.getMember().getUser().getEffectiveAvatarUrl());
        builder.setDescription("Cascade is now shutting down!");
        context.getTypedMessaging().replyInfo(builder);
        CascadeBot.LOGGER.info("Shutting down via command! Issuer: {}", context.getUser().getAsTag());
        ShutdownHandler.stop();
    }

    @Override
    public String command() {
        return "shutdown";
    }

    @Override
    public String description() {
        return "Shuts the bot down.";
    }

    @Override
    public SecurityLevel getCommandLevel() {
        return SecurityLevel.OWNER;
    }

    @Override
    public Module getModule() {
        return Module.DEVELOPER;
    }

}
