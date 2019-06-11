/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.core;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandCore;
import org.cascadebot.cascade.data.Config;

public class PrefixCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 0) {
            String newPrefix = context.getArg(0);

            if (newPrefix.equals("reset")) {
                if (context.hasPermission("prefix.reset")) {
                    context.getSettings().setPrefix(Config.INS.getDefaultPrefix());
                    context.getTypedMessaging().replyInfo("The prefix has been reset to: `%s`", Config.INS.getDefaultPrefix());
                } else {
                    context.getUIMessaging().sendPermissionError("prefix.reset");
                }
                return;
            }

            if (!context.hasPermission("prefix.set")) {
                context.getUIMessaging().sendPermissionError("prefix.set");
                return;
            }

            if (newPrefix.length() > 5) {
                context.getTypedMessaging().replyDanger("The requested prefix is too long!");
                return;
            }
            context.getSettings().setPrefix(newPrefix);
            context.getTypedMessaging().replyInfo("The new prefix is: `%s`", newPrefix);
        } else {
            context.getTypedMessaging().replyInfo("The current server prefix is `%s`", context.getSettings().getPrefix());
        }
    }

    @Override
    public String command() {
        return "prefix";
    }

    @Override
    public String description() {
        return "Gets the current guild prefix";
    }

}
