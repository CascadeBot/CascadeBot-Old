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

public class KickCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyUsage(this);
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(0));

        if (targetMember == null) {
            context.replyDanger(MessagingObjects.getStandardMessageEmbed("Could not find that user!", context.getUser()));
            return;
        }

        String reason = null;
        if (context.getArgs().length > 1) {
            reason = context.getMessage(1);
        }

        CascadeBot.INS.getModerationManager().kick(
                context,
                targetMember,
                sender,
                reason
        );
    }

    @Override
    public String command() {
        return "kick";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Kick Command", "kick", false, Permission.KICK_MEMBERS);
    }

    @Override
    public String description() {
        return "Kicks the specified user";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of(
                "member", "", ArgumentType.REQUIRED, Set.of(
                        Argument.of("reason", "Kicks a member", ArgumentType.OPTIONAL)
                )
        ));
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}
