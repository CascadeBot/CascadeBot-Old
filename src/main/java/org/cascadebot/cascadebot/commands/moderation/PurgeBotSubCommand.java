/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.PurgeCriteria;
import org.cascadebot.cascadebot.utils.PurgeUtils;

public class PurgeBotSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        if (!context.isArgInteger(0)) {
            context.getUiMessaging().replyUsage();
            return;
        }

        PurgeUtils.purge(context, PurgeCriteria.BOT, context.getArgAsInteger(0), null);
    }

    @Override
    public String command() {
        return "bot";
    }

    @Override
    public String parent() { return "purge"; }

}
