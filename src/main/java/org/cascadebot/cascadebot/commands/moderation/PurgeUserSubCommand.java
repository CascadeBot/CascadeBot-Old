/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.PurgeCriteria;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.PurgeUtils;


public class PurgeUserSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        if (!context.isArgInteger(0)) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Member targetMember = null;
        targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(1));
        if (targetMember == null) {
            context.getTypedMessaging().replyDanger(context.i18n("response.cannot_find_user"));
            return;
        }

        PurgeUtils.purge(context, PurgeCriteria.USER, context.getArgAsInteger(0), targetMember.getUser().getId());
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
