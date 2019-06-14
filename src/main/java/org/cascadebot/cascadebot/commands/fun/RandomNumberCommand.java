/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.RandomUtils;
import java.io.IOException;
import java.util.Random;

public class RandomNumberCommand implements ICommandMain {

   
    public void onCommand(Member sender, CommandContext context) {
        Random randomObj = new Random();
        if (context.getArgs().length > 0) {
            if (context.isArgInteger(0) == false) {
                context.getTypedMessaging().replyInfo("Please provide numbers only");
            }
            else {
                int argOne = context.getArgAsInteger(0);
                int randomNumberRaw = RandomUtils.randomNumber(argOne);
                String randomNumber = Integer.toString(randomNumberRaw);
                context.getTypedMessaging().replyInfo("Random number is " + randomNumber);
            }
        } else {
            context.getTypedMessaging().replyDanger("No arguments given");
        }
    }

    @Override
    public String command() {
        return "randnum";
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
