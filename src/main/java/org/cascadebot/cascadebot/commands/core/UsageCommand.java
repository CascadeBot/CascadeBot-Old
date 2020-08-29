/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.core;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.CoreCommand;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.RestrictedCommand;
import org.cascadebot.cascadebot.permissions.Security;

public class UsageCommand extends CoreCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.usage.specify_usage"));
            return;
        }

        MainCommand command = CascadeBot.INS.getCommandManager().getCommand(context.getArg(0), context.getData());
        // If the user isn't authorised to run the command (i.e. it's a dev command) then we pretend it doesn't exist ✨
        if (command == null || (command instanceof RestrictedCommand && !Security.isAuthorised(sender.getIdLong(), ((RestrictedCommand) command).commandLevel()))) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.usage.command_not_found", context.getArg(0)));
            return;
        }

        context.getUiMessaging().replyUsage(command);
    }

    @Override
    public String command() {
        return "usage";
    }

}
