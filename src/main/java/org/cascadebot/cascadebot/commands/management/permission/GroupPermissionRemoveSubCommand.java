/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;

public class GroupPermissionRemoveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "groupperms");
            return;
        }

        if (!CascadeBot.INS.getPermissionsManager().isValidPermission(context.getGuild(), context.getArg(1))) {
            context.getTypedMessaging().replyDanger("`%s` isn't a valid permission", context.getArg(1));
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(0), group -> {
            if (group.removePermission(context.getArg(1))) {
                context.getTypedMessaging().replySuccess("Successfully removed permission `%s` from group `%s`", context.getArg(1), group.getName() + "(" + group.getId() + ")");
            } else {
                context.getTypedMessaging().replyWarning("Couldn't removed permission `%s` from group `%s` as they already have the permission", context.getArg(1), group.getName() + "(" + group.getId() + ")");
            }
        }, sender.getUser().getIdLong());
    }

    @Override
    public String command() {
        return "remove";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Group permissions add sub command", "permissions.group.remove", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
