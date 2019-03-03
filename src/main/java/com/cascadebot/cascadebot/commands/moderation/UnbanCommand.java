/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.shared.Regex;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.util.regex.Pattern;

public class UnbanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyDanger("Not enough arguments (No specified member)");
            return;
        }
        String target = context.getMessage(0);
        if (Regex.ID.matcher(target).matches()) {
            CascadeBot.INS.getShardManager().retrieveUserById(target).queue(user -> {
                unbanUser(context, user);
            }, failure -> {
                context.replyDanger("Could not find user with ID %s!", target);
            });
        } else {
            try {
                context.getGuild().getBanList().queue(list -> {
                    User bannedUser = list.stream()
                            .map(Guild.Ban::getUser)
                            .filter(user -> user.getName().equalsIgnoreCase(target) || user.getAsTag().equalsIgnoreCase(target))
                            .findFirst()
                            .orElse(null);
                    unbanUser(context, bannedUser);
                });
            } catch (InsufficientPermissionException e) {
                context.replyDanger("Cannot get banned user list, missing %s permission!", e.getPermission().getName());
            }
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