/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import net.dv8tion.jda.core.entities.Member;

public class UserInfoCommand implements ICommand {
    @Override
    public void onCommand(Member sender, CommandContext context) {

    }

    @Override
    public String defaultCommand() {
        return "userinfo";
    }

    @Override
    public CommandType getType() {
        return null;
    }
}
