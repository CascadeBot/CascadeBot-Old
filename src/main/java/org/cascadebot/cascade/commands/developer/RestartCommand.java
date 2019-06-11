/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.developer;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.Cascade;
import org.cascadebot.cascade.ShutdownHandler;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandRestricted;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.shared.SecurityLevel;

public class RestartCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (ShutdownHandler.SHUTDOWN_LOCK.get()) {
            context.getTypedMessaging().replyDanger("The bot is already shutting down!");
            return;
        }
        context.reply("Bot is restarting!");
        Cascade.LOGGER.info("Restarting via command! Issuer: " + context.getUser().getAsTag());
        ShutdownHandler.restart();
    }

    @Override
    public String command() {
        return "restart";
    }

    @Override
    public String description() {
        return "Restarts the bot";
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
