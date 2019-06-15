/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.core.entities.Member;

import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.RandomUtils;

public class RandomNumberCommand implements ICommandMain {

    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 0) {
            if (!context.isArgInteger(0)) {
                context.getTypedMessaging().replyDanger("Please provide numbers only");
            } else {
                int range = context.getArgAsInteger(0);
                int randomNumber = RandomUtils.randomNumber(range); 
                context.getTypedMessaging().replyInfo("Random number is `" + randomNumber + "`");
            }
        } else {
             int randomNumber = RandomUtils.randomNumber(10);
             context.getTypedMessaging().replyInfo("Random number is `" + randomNumber + "`");
        }
    }

    @Override
    public String command() {
        return "random";
    }

    @Override
    public Module getModule() {
        return Module.FUN;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Random number command", "randnum", true);
    }

    @Override
    public String description() {
        return "Returns a random number";
    }
}
