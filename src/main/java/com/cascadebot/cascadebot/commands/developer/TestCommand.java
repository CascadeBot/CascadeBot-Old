/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import net.dv8tion.jda.core.entities.Member;

public class TestCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        /*

            Here lies the infamous test command, please don't commit stuff to this!

         */
    }

    @Override
    public String command() {
        return "test";
    }

    @Override
    public CommandType getType() {
        return CommandType.DEVELOPER;
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

}
