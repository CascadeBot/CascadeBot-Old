/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.PurgeUtils;

import java.util.Set;

public class PurgeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        if (!context.isArgInteger(0)) {
            context.getUiMessaging().replyUsage(this);
            return;
        }

        PurgeUtils.purge(context, PurgeUtils.Criteria.ALL, context.getArgAsInteger(0), null);
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

    @Override
    public String command() {
        return "purge";
    }

    @Override
    public Set<ISubCommand> getSubCommands() {
        return Set.of(new PurgeContainSubCommand(), new PurgeBotSubCommand(), new PurgeAttachmentSubCommand(), new PurgeLinkSubCommand(), new PurgeUserSubCommand());
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("purge", false, Permission.MESSAGE_MANAGE);
    }

}
