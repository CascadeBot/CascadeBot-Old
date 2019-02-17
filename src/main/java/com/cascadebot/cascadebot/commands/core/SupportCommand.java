/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.IMainCommand;
import com.cascadebot.cascadebot.permissions.Permission;
import net.dv8tion.jda.core.entities.Member;

public class SupportCommand implements IMainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply("Cascade Support Server: "+Constants.serverInvite);
    }

    @Override
    public String command() {
        return "support";
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
        return null;
    }

}