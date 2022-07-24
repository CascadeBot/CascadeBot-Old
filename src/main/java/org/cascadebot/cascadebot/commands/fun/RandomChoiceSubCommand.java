/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.utils.RandomUtils;

public class RandomChoiceSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 1) {
            context.getTypedMessaging().replySuccess(context.i18n("commands.random.random_choice_result", RandomUtils.randomChoice(context.getArgs())));
        } else {
            context.getUiMessaging().replyUsage();
        }
    }

    @Override
    public String command() { return "choice"; }

    @Override
    public String parent() { return "random"; }

}
