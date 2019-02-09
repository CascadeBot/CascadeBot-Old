/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.permissions.Permission;
import net.dv8tion.jda.core.entities.Member;

public class InviteCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        // context.reply(CascadeBot.getInvite());
    }

    @Override
    public String command() {
        return "invite";
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

    @Override
    public Module getType() {
        return Module.CORE;
    }

    @Override
    public Permission getPermission() {
        return null; // Cannot be restricted
    }

}
