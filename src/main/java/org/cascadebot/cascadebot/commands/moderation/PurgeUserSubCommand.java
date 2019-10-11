/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.*;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.PurgeUtils;


public class PurgeUserSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage();
            return;
        }
        try {
            context.getArgAsInteger(0);
        } catch (NumberFormatException error) {
            context.getUIMessaging().replyUsage(this);
            return;
        }

        Member targetMember = null;
        targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(1));
        if (targetMember == null) {
            context.getTypedMessaging().replyDanger(context.i18n("response.cannot_find_user"));
        }

        PurgeUtils.purge(context, PurgeUtils.Criteria.USER, context.getArgAsInteger(0), targetMember.getUser().getId());
    }

    @Override
    public String command() {
        return "user";
    }

    @Override
    public String parent() { return "purge"; }

    @Override
    public CascadePermission getPermission() { return null; }

}