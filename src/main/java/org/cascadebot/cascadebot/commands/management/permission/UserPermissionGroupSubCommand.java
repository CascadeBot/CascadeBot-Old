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
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class UserPermissionGroupSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 3) {
            context.getUIMessaging().replyUsage(this, "userperms");
            return;
        }

        if (!context.getArg(0).equalsIgnoreCase("put") && !context.getArg(0).equalsIgnoreCase("remove")) {
            context.getUIMessaging().replyUsage(this, "userperms");
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(1));
        if (member == null) {
            context.getTypedMessaging().replyDanger("User `" + context.getArg(1) + "` not found");
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(2), group -> {
            User user = context.getData().getPermissions().getPermissionUser(member);
            if (context.getArg(0).equalsIgnoreCase("put")) {
                if (user.addGroup(group)) {
                    context.getTypedMessaging().replySuccess("Put user `%s` in group `%s`", member.getUser().getAsTag(), group.getName());
                } else {
                    context.getTypedMessaging().replyDanger("Couldn't add user `%s` to group `%s` because they're already in the group");
                }
            } else if (context.getArg(0).equalsIgnoreCase("remove")) {
                if (user.removeGroup(group)) {
                    context.getTypedMessaging().replySuccess("Removed user `%s` from group `%s`", member.getUser().getAsTag(), group.getName());
                } else {
                    context.getTypedMessaging().replyDanger("Couldn't remove user `%s` from group %s` because they're not in the group");
                }
            }
        }, sender.getUser().getIdLong());


    }

    @Override
    public String command() {
        return "group";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("User permissions group sub command", "permissions.user.group", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        Set<Argument> subArgs = Set.of(Argument.of("user", null, ArgumentType.REQUIRED, Set.of(Argument.of("group", "Add/Remove a user from a group", ArgumentType.REQUIRED))));
        return Set.of(Argument.of("put", null, ArgumentType.COMMAND, subArgs), Argument.of("remove", null, ArgumentType.COMMAND, subArgs));
    }

}
