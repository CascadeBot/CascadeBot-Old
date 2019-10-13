/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.PurgeUtils;


public class PurgeAttachmentSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        if (!context.isArgInteger(0)) {
            context.getUIMessaging().replyUsage(this);
            return;
        }

        PurgeUtils.purge(context, PurgeUtils.Criteria.ATTACHMENT, context.getArgAsInteger(0), null);
    }

    @Override
    public String command() {
        return "attachment";
    }

    @Override
    public String parent() { return "purge"; }

    @Override
    public CascadePermission getPermission() { return null; }

}
