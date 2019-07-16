/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.core;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.Environment;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandCore;

public class InviteCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (Environment.isProduction()) {
            context.getDirectMessageMessaging().replyDM(CascadeBot.getInvite(), true);
            context.getTypedMessaging().replyInfo("We've sent you a DM with more information!");
        } else {
            context.getDirectMessageMessaging().replyDM("https://www.youtube.com/watch?v=ARJ8cAGm6JE");
            context.getTypedMessaging().replyInfo("We've sent you a DM with more information!");
        }
    }

    @Override
    public String command() {
        return "invite";
    }

}
