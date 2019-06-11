/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.core;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.Cascade;
import org.cascadebot.cascade.commandmeta.Argument;
import org.cascadebot.cascade.commandmeta.ArgumentType;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandCore;
import org.cascadebot.cascade.commandmeta.ICommandMain;

import java.util.Set;

public class UsageCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getTypedMessaging().replyDanger("Please specify command to get usage from");
            return;
        }

        ICommandMain command = Cascade.INS.getCommandManager().getCommand(context.getArg(0), sender.getUser(), context.getData());
        if (command == null) {
            context.getTypedMessaging().replyDanger("Command `%s` not found!", context.getArg(0));
            return;
        }

        context.getTypedMessaging().replyInfo(context.getUsage(command));
    }

    @Override
    public String command() {
        return "usage";
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public Set<Argument> getArguments() {
        return Set.of(Argument.of("command", "Gets the usage for a command", ArgumentType.REQUIRED));
    }

}
