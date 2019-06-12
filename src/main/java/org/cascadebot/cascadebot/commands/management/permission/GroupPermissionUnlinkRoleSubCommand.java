/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class GroupPermissionUnlinkRoleSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "groupperms");
            return;
        }

        Role role = DiscordUtils.getRole(context.getArg(1), context.getGuild());
        if (role == null) {
            context.getTypedMessaging().replyDanger("Could not find role " + context.getArg(1));
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(0), group -> {
            if (group.unlinkRole(role.getIdLong())) {
                context.getTypedMessaging().replySuccess("Unlinked group `" + group.getName() + "` to role `" + role.getName() + "`");
            } else {
                context.getTypedMessaging().replyWarning("Couldn't unlink group `" + group.getName() + "` to role `" + role.getName() + "`");
            }
        }, sender.getUser().getIdLong());
    }

    @Override
    public String command() {
        return "unlink";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Group permissions unlink sub command", "permissions.group.unlink", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
