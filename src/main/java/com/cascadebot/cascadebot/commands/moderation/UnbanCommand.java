/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.moderation.ModAction;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.pagination.Page;
import com.cascadebot.shared.Regex;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.util.List;

public class UnbanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyDanger("Not enough arguments (No specified member)");
            return;
        }

        String target = context.getArg(0);
        String reason = null;
        if (context.getArgs().length > 1) {
            reason = context.getMessage(1);
        }

        List<User> bannedUsers = FinderUtil.findBannedUsers(target, context.getGuild());

        if (bannedUsers.size() == 0) {
            context.replyDanger("Could not find a user to unban matching: %s", target);
        } else if (bannedUsers.size() == 1) {
            CascadeBot.INS.getModerationManager().unban(
                    context,
                    bannedUsers.get(0),
                    sender,
                    reason
            );
        } else {
            context.replyDanger("There is more than one user that matches this criteria! Please enter the ID or the user's full name!");
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
    public CascadePermission getPermission() {
        return CascadePermission.of("Unban Command", "ban",
                false, Permission.BAN_MEMBERS);
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}