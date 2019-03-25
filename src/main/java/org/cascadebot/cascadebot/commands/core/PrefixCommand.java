/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.core;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandCore;
import org.cascadebot.cascadebot.data.Config;

public class PrefixCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 0) {
            String newPrefix = context.getArg(0);

            if (newPrefix.equals("reset")) {
                if (context.hasPermission("prefix.reset")) {
                    context.getData().setPrefix(Config.INS.getDefaultPrefix());
                    context.replyInfo("The prefix has been reset to: `%s`", Config.INS.getDefaultPrefix());
                } else {
                    context.sendPermissionsError("prefix.reset");
                }
                return;
            }

            if (!context.hasPermission("prefix.set")) {
                context.sendPermissionsError("prefix.set");
                return;
            }

            if (newPrefix.length() > 5) {
                context.replyDanger("The requested prefix is too long!");
                return;
            }
            context.getData().setPrefix(newPrefix);
            context.replyInfo("The new prefix is: `%s`", newPrefix);
        } else {
            context.replyInfo("The current server prefix is `%s`", context.getData().getPrefix());
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
