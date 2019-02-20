/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

public class UnbanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyDanger("Not enough arguments (No specified member)");
            return;
        }
        String targetUser = context.getMessage(0);
        try {
            context.getGuild().getController().unban(targetUser).queue();
            context.replyInfo("User <@" + targetUser + "> has been Unbanned!");
        } catch (InsufficientPermissionException e) {
            context.replyWarning("Cannot unban user <@" + targetUser + ">, missing Ban Members permission");
        }

    }

    @Override
    public String command() {
        return "unban";
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
