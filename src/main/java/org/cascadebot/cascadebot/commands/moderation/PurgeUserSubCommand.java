/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.PurgeCriteria;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.PurgeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PurgeUserSubCommand extends SubCommand {

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

        List<Member> members = new ArrayList<>();

        for (int i = 0; i < context.getArgs().length; i++) {
            if (i < 1) continue;
            Member targetMember = null;
            targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(i));
            if (targetMember == null) {
                continue;
            }
            members.add(targetMember);
        }

        if (members.isEmpty()) {
            context.getTypedMessaging().replyDanger(context.i18n("response.cannot_find_user"));
            return;
        }

        PurgeUtils.purge(context, PurgeCriteria.USER, context.getArgAsInteger(0), members.stream().map(Member::getId).collect(Collectors.joining(" ")));
    }

    @Override
    public String command() {
        return "user";
    }

    @Override
    public String parent() { return "purge"; }

    @Override
    public CascadePermission permission() { return null; }

}
