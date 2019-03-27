/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.List;
import java.util.Set;

public class UnbanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyUsage(this);
            return;
        }

        String target = context.getArg(0);
        String reason = null;
        if (context.getArgs().length > 1) {
            reason = context.getMessage(1);
        }

        List<User> bannedUsers = FinderUtil.findBannedUsers(target, context.getGuild());

        if (bannedUsers.size() == 0) {
            context.getTypedMessaging().replyDanger(MessagingObjects.getStandardMessageEmbed("Could not find a user to unban matching: " + target, context.getUser()));
        } else if (bannedUsers.size() == 1) {
            CascadeBot.INS.getModerationManager().unban(
                    context,
                    bannedUsers.get(0),
                    sender,
                    reason
            );
        } else {
            context.getTypedMessaging().replyDanger(MessagingObjects.getStandardMessageEmbed("There is more than one user that matches this criteria!" +
                    " Please enter the ID or the user's full name!", context.getUser()));
        }

    }

    @Override
    public String command() {
        return "unban";
    }

    @Override
    public String description() {
        return "Unbans a user";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of(
                "user",
                "",
                ArgumentType.REQUIRED,
                Set.of(
                        Argument.of("reason", "Unbans a user", ArgumentType.OPTIONAL)
                )));
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Unban Command", "ban",
                false, Permission.BAN_MEMBERS);
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}