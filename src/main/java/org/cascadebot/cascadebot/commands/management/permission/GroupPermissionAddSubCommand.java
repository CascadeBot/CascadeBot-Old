/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;

public class GroupPermissionAddSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage();
            return;
        }

        if (!CascadeBot.INS.getPermissionsManager().isValidPermission(context.getGuild(), context.getArg(1))) {
            context.getTypedMessaging().replyDanger("`%s` isn't a valid permission", context.getArg(1));
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(0), group -> {
            if (group.addPermission(context.getArg(1))) {
                context.getTypedMessaging().replySuccess("Successfully added permission `%s` to group `%s`", context.getArg(1), group.getName() + "(" + group.getId() + ")");
            } else {
                context.getTypedMessaging().replyWarning("Couldn't add permission `%s` to group `%s` as they already have the permission", context.getArg(1), group.getName() + "(" + group.getId() + ")");
            }
        }, sender.getUser().getIdLong());
    }

    @Override
    public String command() {
        return "add";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("permissions.group.add", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
