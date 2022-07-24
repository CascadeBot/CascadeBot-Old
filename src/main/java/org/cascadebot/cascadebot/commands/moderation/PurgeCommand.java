/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.PurgeCriteria;
import org.cascadebot.cascadebot.utils.PurgeUtils;

import java.util.Set;

public class PurgeCommand extends MainCommand {

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

        PurgeUtils.purge(context, PurgeCriteria.ALL, context.getArgAsInteger(0), null);
    }

    @Override
    public Module module() {
        return Module.MODERATION;
    }

    @Override
    public String command() {
        return "purge";
    }

    @Override
    public Set<SubCommand> subCommands() {
        return Set.of(new PurgeContainSubCommand(), new PurgeBotSubCommand(), new PurgeAttachmentSubCommand(), new PurgeLinkSubCommand(), new PurgeUserSubCommand());
    }

}
