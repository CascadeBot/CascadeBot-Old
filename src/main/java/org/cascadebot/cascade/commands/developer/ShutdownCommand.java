/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.developer;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.Cascade;
import org.cascadebot.cascade.Environment;
import org.cascadebot.cascade.ShutdownHandler;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandRestricted;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.messaging.MessageType;
import org.cascadebot.cascade.messaging.MessagingObjects;
import org.cascadebot.cascade.utils.ConfirmUtils;
import org.cascadebot.shared.SecurityLevel;

public class ShutdownCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        // A confirmation check to make sure we actually want to shut down on production
        if (Environment.isProduction()) {
            if (!ConfirmUtils.hasConfirmedAction("shutdown_bot", sender.getUser().getIdLong())) {
                ConfirmUtils.confirmAction(
                        sender.getUser().getIdLong(),
                        "shutdown_bot",
                        context.getChannel(),
                        MessageType.DANGER,
                        "It looks like the bot is running in ***production*** mode, **do you _really_ want to do this?** \n" +
                                "If so, simply repeat the command again. This confirmation will expire in one minute!",
                        new ConfirmUtils.ConfirmRunnable() {
                            @Override
                            public void execute() {
                                shutdown(context);
                            }
                        });
                return;
            }
            ConfirmUtils.completeAction("shutdown_bot", sender.getUser().getIdLong());
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
        Cascade.LOGGER.info("Shutting down via command! Issuer: " + context.getUser().getAsTag());
        ShutdownHandler.stop();
    }

    @Override
    public String command() {
        return "shutdown";
    }

    @Override
    public String description() {
        return "Stops the bot";
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
