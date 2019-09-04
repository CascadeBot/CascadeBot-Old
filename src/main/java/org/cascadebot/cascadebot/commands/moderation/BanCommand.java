/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.moderation.ModAction;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ConfirmUtils;
import org.cascadebot.cascadebot.utils.DiscordUtils;

import java.util.Set;

public class BanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUIMessaging().replyUsage();
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        User targetUser;
        String reason = null;

        if (context.getArgs().length >= 2) {
            reason = context.getMessage(1);
        }

        if (targetMember == null) {
            // If the member is null, the user does not exist in the guild.
            // This attempts to retrieve the user from Discord.
            targetUser = DiscordUtils.getUser(context.getGuild(), context.getMessage(0), true);

            if (targetUser == null) {
                // We couldn't find user from a member or from discord so just end here
                context.getTypedMessaging().replyDanger(context.i18n("responses.cannot_find_user"));
                return;
            }

            String finalReason = reason;
            ConfirmUtils.confirmAction(
                    sender.getIdLong(),
                    "forceban_user",
                    context.getChannel(),
                    MessageType.DANGER,
                    context.i18n("commands.ban.forcefully_ban"),
                    new ConfirmUtils.ConfirmRunnable() {
                        @Override
                        public void execute() {
                            CascadeBot.INS.getModerationManager().ban(
                                    context,
                                    ModAction.FORCE_BAN,
                                    targetUser,
                                    sender,
                                    finalReason,
                                    7 // TODO: add this as an arg
                            );
                        }
                    });
            return;
        } else {
            // If the member is not null, we can safely get the user from the member.
            targetUser = targetMember.getUser();
        }

        CascadeBot.INS.getModerationManager().ban(
                context,
                ModAction.BAN,
                targetUser,
                sender,
                reason,
                7 // TODO: add this as an arg
        );
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

    @Override
    public String command() {
        return "ban";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("ban", false, Permission.BAN_MEMBERS);
    }

}
