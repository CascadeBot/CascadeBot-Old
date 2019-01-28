/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.permissions.SecurityLevel;
import com.cascadebot.cascadebot.utils.ErrorUtils;
import net.dv8tion.jda.core.entities.Member;

import java.util.stream.Collectors;

public class DumpCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArg(0).equalsIgnoreCase("threads")) {
            String threads = Thread.getAllStackTraces().keySet().stream().map(Thread::getName).sorted().collect(Collectors.joining("\n"));
            if (threads.length() > 2048) {
                context.reply(ErrorUtils.paste(threads));
            } else {
                context.reply(threads);
            }
        }
    }

    @Override
    public String command() {
        return "dump";
    }

    @Override
    public CommandType getType() {
        return CommandType.DEVELOPER;
    }

    @Override
    public SecurityLevel getCommandLevel() {
        return SecurityLevel.DEVELOPER;
    }

}
