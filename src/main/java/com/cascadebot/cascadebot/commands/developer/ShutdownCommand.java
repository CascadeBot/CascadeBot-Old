/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.ShutdownHandler;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.permissions.SecurityLevel;
import net.dv8tion.jda.core.entities.Member;

public class ShutdownCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadeBot.logger.info("Shutting down via command!");
        ShutdownHandler.stop();
    }

    @Override
    public String defaultCommand() {
        return "shutdown";
    }

    @Override
    public SecurityLevel getCommandLevel() { return SecurityLevel.OWNER; }

    @Override
    public CommandType getType() {
        return CommandType.OWNER;
    }



    @Override
    public boolean forceDefault() {
        return true;
    }
}
