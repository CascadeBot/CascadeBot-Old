/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.fun;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.IMainCommand;
import com.cascadebot.cascadebot.permissions.Permission;
import net.dv8tion.jda.core.entities.Member;

public class CatCommand implements IMainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply("https://giphy.com/gifs/cat-33OrjzUFwkwEg");
    }

    @Override
    public String command() {
        return "cat";
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Permission getPermission() {
        return null; // Cannot be restricted
    }

}
