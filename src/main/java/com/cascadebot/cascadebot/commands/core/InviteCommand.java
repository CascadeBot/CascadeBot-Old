/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommand;
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

}
