/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class UserPermissionTestSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "userperms");
            return;
        }

        Member target = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (target == null) {
            context.getTypedMessaging().replyDanger("User `" + context.getArg(0) + "` not found");
            return;
        }

        CascadePermission perm = CascadeBot.INS.getPermissionsManager().getPermission(context.getArg(1));
        if (perm == null) {
            context.getTypedMessaging().replyDanger("Permission `" + context.getArg(1) + "` isn't a valid permission");
            return;
        }

        if (context.getData().getPermissions().hasPermission(target, context.getChannel(), perm, context.getSettings())) {
            context.getTypedMessaging().replyInfo(UnicodeConstants.TICK + " User " + target.getUser().getAsTag() + " has the permission `" + perm.getPermission() + "`");
        } else {
            context.getTypedMessaging().replyInfo(UnicodeConstants.RED_CROSS + " User " + target.getUser().getAsTag() + " doesn't the permission `" + perm.getPermission() + "`");
        }
    }

    @Override
    public String command() {
        return "test";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("User permissions test sub command", "permissions.user.test", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
