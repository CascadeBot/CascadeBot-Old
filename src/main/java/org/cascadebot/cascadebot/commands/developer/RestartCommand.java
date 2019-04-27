/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.shared.SecurityLevel;

public class RestartCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply(context.i18n("commands.developer.restart.restarting"));
        CascadeBot.LOGGER.info(context.i18n("commands.developer.restart.restarting_console", context.getUser().getAsTag()));
        ShutdownHandler.restart();
    }

    @Override
    public String command() {
        return "restart";
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
