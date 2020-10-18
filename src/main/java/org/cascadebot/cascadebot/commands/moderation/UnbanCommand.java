/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.List;

public class UnbanCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String target = context.getArg(0);
        String reason = null;
        if (context.getArgs().length > 1) {
            reason = context.getMessage(1);
        }

        List<User> bannedUsers = FinderUtil.findBannedUsers(target, context.getGuild());

        if (bannedUsers.size() == 0) {
            context.getTypedMessaging().replyDanger(MessagingObjects.getStandardMessageEmbed(context.i18n("responses.cannot_find_user_matching", target), context.getUser(), context.getLocale()));
        } else if (bannedUsers.size() == 1) {
            CascadeBot.INS.getModerationManager().unban(
                    context,
                    bannedUsers.get(0),
                    sender,
                    reason
            );
        } else {
            context.getTypedMessaging().replyDanger(MessagingObjects.getStandardMessageEmbed(context.i18n("commands.unban.several_matches"), context.getUser(), context.getLocale()));
        }

    }

    @Override
    public String command() {
        return "unban";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("ban",
                false, Permission.BAN_MEMBERS);
    }

    @Override
    public Module module() {
        return Module.MODERATION;
    }

}