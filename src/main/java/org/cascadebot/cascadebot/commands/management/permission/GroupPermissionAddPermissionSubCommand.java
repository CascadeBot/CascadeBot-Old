/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import java.util.Set;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class GroupPermissionAddPermissionSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if(context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "groupperms");
            return;
        }

        if(!CascadeBot.INS.getPermissionsManager().isValidPermission(context.getGuild(), context.getArg(1))) {
            context.getTypedMessaging().replyDanger("`%s` isn't a valid permission", context.getArg(1));
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(0), group -> {
            if(group.addPermission(context.getArg(1))) {
                context.getTypedMessaging().replySuccess("Successfully added permission `%s` to group `%s`", context.getArg(1), group.getName() + "(" + group.getId() + ")");
            } else {
                context.getTypedMessaging().replyWarning("Couldn't add permission `%s` to group `%s` as they already have the permission", context.getArg(1), group.getName() + "(" + group.getId() + ")");
            }
        }, sender.getUser().getIdLong());
    }

    @Override
    public String command() {
        return "addpermission";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Group permissions add sub command", "permissions.group.addpermission", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }
}
