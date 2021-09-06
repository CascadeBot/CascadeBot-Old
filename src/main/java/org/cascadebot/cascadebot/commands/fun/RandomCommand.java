/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.RandomUtils;

import java.util.Set;

public class RandomCommand extends MainCommand {

    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 1) {
            if (!context.isArgInteger(0)) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.random.numbers_only"));
            } else {
                int range = context.getArgAsInteger(0);
                if (range < 1) {
                    context.getTypedMessaging().replyDanger(context.i18n("commands.random.no_negatives"));
                    return;
                }
                // Random number between 1 and range (Inclusive for both)
                int randomNumber = RandomUtils.randomNumber(1, range + 1);
                context.getTypedMessaging().replyInfo(context.i18n("commands.random.random_result", randomNumber));
            }
        } else if (context.getArgs().length >= 2) {
            if (!context.isArgInteger(0) || !context.isArgInteger(1) ) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.random.numbers_only"));
            } else {
                int min = context.getArgAsInteger(0);
                int max = context.getArgAsInteger(1);
                int randomNumber = RandomUtils.randomNumber(min, max + 1);
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
    public Module module() {
        return Module.FUN;
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("random", true);
    }

    @Override
    public Set<DeprecatedSubCommand> subCommands() {
        return Set.of(new RandomChoiceSubCommand(), new RandomColorSubCommand());
    }

}
