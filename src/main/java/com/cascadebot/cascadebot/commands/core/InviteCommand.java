/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.permissions.Permission;
import net.dv8tion.jda.core.entities.Member;

public class InviteCommand implements ICommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
    //    context.reply(CascadeBot.getInvite());
    }

    @Override
    public String defaultCommand() {
        return "invite";
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

    @Override
    public CommandType getType() {
        return CommandType.CORE;
    }

    @Override
    public Permission getPermission() {
        return null; // Cannot be restricted
    }

}
