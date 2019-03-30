/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
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
            context.getTypedMessaging().replyDanger("Not enough arguments (No specified member)");
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (targetMember == null) {
            context.getTypedMessaging().replyDanger(MessagingObjects.getStandardMessageEmbed("Could not find that user!", context.getUser()));
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
        return CascadePermission.of("Soft-ban Command", "softban",
                false, Permission.BAN_MEMBERS);
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of(
                "member", "", ArgumentType.REQUIRED, Set.of(
                        Argument.of("reason", "Soft-bans a member", ArgumentType.OPTIONAL)
                )
        ));
    }


    @Override
    public String description() {
        return "Bans and then unbans a specified user in order to remove messages and kick the user";
    }


    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}
