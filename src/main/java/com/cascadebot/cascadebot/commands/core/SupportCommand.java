/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import net.dv8tion.jda.core.entities.Member;

public class SupportCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.replyInfo("Cascade Support Server: " + Constants.serverInvite);
    }

    @Override
    public String command() {
        return "support";
    }

}