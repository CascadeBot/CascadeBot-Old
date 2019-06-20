/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Group;

public class GroupPermissionCreateSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this, "groupperms");
            return;
        }

        Group group = context.getData().getPermissions().createGroup(context.getArg(0));
        context.getTypedMessaging().replySuccess("Created group `%s` with id `%s`", context.getArg(0), group.getId());
    }

    @Override
    public String command() {
        return "create";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Group permissions create sub command", "permissions.group.create", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
