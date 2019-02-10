/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import net.dv8tion.jda.core.entities.Member;

public class InviteCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        // context.reply(CascadeBot.getInvite());
    }

    @Override
    public String command() {
        return "invite";
    }

}
