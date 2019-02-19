/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.utils.PasteUtils;
import com.cascadebot.shared.SecurityLevel;
import net.dv8tion.jda.core.entities.Member;

import java.util.stream.Collectors;

public class DumpCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.replyDanger("Hmmm either pick: `threads`, `commands` or `permissions`");
            return;
        }
        if (context.getArg(0).equalsIgnoreCase("threads")) {
            String threads = Thread.getAllStackTraces().keySet().stream().map(Thread::getName).sorted().collect(Collectors.joining("\n"));
            PasteUtils.pasteIfLong(threads, 2048, context::reply);
        } else if (context.getArg(0).equalsIgnoreCase("commands")) {
            StringBuilder builder = new StringBuilder();
            for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommands()) {
                builder.append(command.command()).append(" `").append(command.getModule().toString().toLowerCase()).append("`");
                for (ICommandExecutable subcommand : command.getSubCommands()) {
                    builder.append("\n - ").append(subcommand.command());
                }
                builder.append("\n");
            }
            PasteUtils.pasteIfLong(builder.toString(), 2048, context::reply);
        }
    }

    @Override
    public String command() {
        return "dump";
    }

    @Override
    public Module getModule() {
        return Module.DEVELOPER;
    }

    @Override
    public SecurityLevel getCommandLevel() {
        return SecurityLevel.DEVELOPER;
    }

}
