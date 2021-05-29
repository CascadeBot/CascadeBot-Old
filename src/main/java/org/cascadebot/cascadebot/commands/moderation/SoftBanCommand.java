/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.moderation.ModAction;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class SoftBanCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.no_specified_user"));
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (targetMember == null) {
            context.getTypedMessaging().replyDanger(MessagingObjects.getStandardMessageEmbed(context.i18n("responses.cannot_find_user"), context.getUser(), context.getLocale()));
            return;
        }

        String reason = null;

        if (context.getArgs().length >= 2) {
            reason = context.getMessage(1);
        }

        CascadeBot.INS.getModerationManager().ban(
                context,
                ModAction.SOFT_BAN,
                targetMember.getUser(),
                sender,
                reason,
                7 // TODO
        );

    }

    @Override
    public String command() {
        return "softban";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("softban",
                false, Permission.BAN_MEMBERS);
    }

    @Override
    public Module module() {
        return Module.MODERATION;
    }

}
