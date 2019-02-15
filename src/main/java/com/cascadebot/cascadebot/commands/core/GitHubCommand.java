/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandCore;
import net.dv8tion.jda.core.entities.Member;

public class GitHubCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply("https://github.com/CascadeBot/CascadeBot");
    }

    @Override
    public String command() {
        return "github";
    }

}
