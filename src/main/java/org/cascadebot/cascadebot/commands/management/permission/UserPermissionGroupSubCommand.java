/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import java.util.Set;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;

public class UserPermissionGroupSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 3) {
            context.getUIMessaging().replyUsage();
            return;
        }

        if (!context.getArg(0).equalsIgnoreCase("put") && !context.getArg(0).equalsIgnoreCase("remove")) {
            context.getUIMessaging().replyUsage();
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(1));
        if (member == null) {
            context.getTypedMessaging().replyDanger("User `%s` not found", context.getArg(1));
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(2), group -> {
            User user = context.getData().getPermissions().getPermissionUser(member);
            if (context.getArg(0).equalsIgnoreCase("put")) {
                if (user.addGroup(group)) {
                    context.getTypedMessaging().replySuccess("Put user `%s` in group `%s`", member.getUser().getAsTag(), group.getName());
                } else {
                    context.getTypedMessaging().replyWarning("Couldn't add user `%s` to group `%s` because they're already in the group");
                }
            } else if (context.getArg(0).equalsIgnoreCase("remove")) {
                if (user.removeGroup(group)) {
                    context.getTypedMessaging().replySuccess("Removed user `%s` from group `%s`", member.getUser().getAsTag(), group.getName());
                } else {
                    context.getTypedMessaging().replyWarning("Couldn't remove user `%s` from group %s` because they're not in the group");
                }
            }
        }, sender.getUser().getIdLong());


    }

    @Override
    public String command() {
        return "group";
    }

    @Override
    public String parent() {
        return "userperms";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("permissions.user.group", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
