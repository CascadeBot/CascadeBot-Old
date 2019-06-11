/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.moderation;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.cascadebot.cascade.Cascade;
import org.cascadebot.cascade.commandmeta.Argument;
import org.cascadebot.cascade.commandmeta.ArgumentType;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandMain;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.messaging.MessageType;
import org.cascadebot.cascade.moderation.ModAction;
import org.cascadebot.cascade.permissions.CascadePermission;
import org.cascadebot.cascade.utils.ConfirmUtils;
import org.cascadebot.cascade.utils.DiscordUtils;

import java.util.Set;

public class BanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUIMessaging().replyUsage(this);
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
                context.getTypedMessaging().replyDanger("We could not find that user!");
                return;
            }

            String finalReason = reason;
            ConfirmUtils.confirmAction(
                    sender.getUser().getIdLong(),
                    "forceban_user",
                    context.getChannel(),
                    MessageType.DANGER,
                    "**We couldn't find that user in this guild!** \n" +
                            "If you would like to forcefully ban them, please react to this message!",
                    new ConfirmUtils.ConfirmRunnable() {
                        @Override
                        public void execute() {
                            Cascade.INS.getModerationManager().ban(
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

        Cascade.INS.getModerationManager().ban(
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
    public String description() {
        return "Bans or forcebans the specified user";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of(
                "member", "", ArgumentType.REQUIRED, Set.of(
                        Argument.of("reason", "Bans a member and can optionally forcefully ban a user", ArgumentType.OPTIONAL)
                )
        ));
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Ban command", "ban", false, Permission.BAN_MEMBERS);
    }

}
