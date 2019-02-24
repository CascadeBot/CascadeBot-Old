/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.Environment;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import net.dv8tion.jda.core.entities.Member;

public class InviteCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (Environment.isProduction()) {
            context.replyDM(CascadeBot.getInvite(), true);
        } else {
            context.replyDM("https://www.youtube.com/watch?v=ARJ8cAGm6JE");
        }
    }

    @Override
    public String command() {
        return "invite";
    }

    @Override
    public String description() {
        return "Gets the bot invite link";
    }

}
