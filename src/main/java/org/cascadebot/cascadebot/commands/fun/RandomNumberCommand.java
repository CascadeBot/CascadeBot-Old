/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.api.entities.Member;

import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.RandomUtils;

import java.util.Set;

public class RandomNumberCommand implements ICommandMain {

    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 0) {
            if (!context.isArgInteger(0)) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.random.numbers_only"));
            } else {
                int range = context.getArgAsInteger(0);
                if (range < 1) {
                    context.getTypedMessaging().replyDanger(context.i18n("commands.random.no_negatives"));
                    return;
                }
                int randomNumber = RandomUtils.randomNumber(range);
                context.getTypedMessaging().replyInfo(context.i18n("commands.random.random_result", randomNumber));
            }
        } else {
            int randomNumber = RandomUtils.randomNumber(10);
            context.getTypedMessaging().replyInfo(context.i18n("commands.random.random_result", randomNumber));
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
        return CascadePermission.of("random", true);
    }

    @Override
    public Set<ISubCommand> getSubCommands() {
        return Set.of(new RandomChoiceSubCommand(), new RandomColorSubCommand());
    }

}
