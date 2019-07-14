/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

import java.util.Set;

public class SoftBanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.no_specified_user"));
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (targetMember == null) {
            context.getTypedMessaging().replyDanger(MessagingObjects.getStandardMessageEmbed(context.i18n("responses.cannot_find_user"), context.getUser()));
            return;
        }

        String reason = null;

        if (context.getArgs().length >= 2) {
            reason = context.getMessage(1);
        }

        CascadeBot.INS.getModerationManager().softBan(
                context,
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
    public CascadePermission getPermission() {
        return CascadePermission.of("softban",
                false, Permission.BAN_MEMBERS);
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}
