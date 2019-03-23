/*
 *
 *  * Copyright (c) 2019 CascadeBot. All rights reserved.
 *  * Licensed under the MIT license.
 *
 */

package com.cascadebot.cascadebot.commands.fun;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.entities.Member;

public class MCSkinCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String playerNameArg;
        String finalLink;

        if (context.getArgs().length == 0) {
            context.replyDanger("Please provide a username!");
        } else if (context.getArgs().length != 0) {
            playerNameArg = context.getArg(0);
            finalLink = "https://visage.weeryan17.com/full/256/" + playerNameArg;
            context.reply(finalLink);
        }
    }

    @Override
    public Module getModule() {
        return Module.FUN;
    }

    @Override
    public String command() {
        return "skin";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Skin command", "skin", true);
    }

    @Override
    public String description() {
        return "MC Skin Command";
    }

}
