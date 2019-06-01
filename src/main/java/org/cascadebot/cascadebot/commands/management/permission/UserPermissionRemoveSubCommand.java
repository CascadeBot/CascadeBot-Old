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
import org.cascadebot.cascadebot.permissions.PermissionsManager;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class UserPermissionRemoveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if(context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "userperms");
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(0)); //TODO Switch these of over to the error handler in language
        if(member == null) {
            context.getTypedMessaging().replyDanger("User `" + context.getArg(0) + "` not found");
            return;
        }

        User user = context.getData().getPermissions().getPermissionUser(member);

        if(user.removePermission(context.getArg(1))) {
                context.getTypedMessaging().replySuccess("Successfully removed permission `%s` to user `%s`", context.getArg(1), member.getUser().getAsTag());
        } else {
            context.getTypedMessaging().replyWarning("Couldn't remove permission `%s` to user `%s` as they don't have the permission", context.getArg(1), member.getUser().getAsTag());
        }
    }

    @Override
    public String command() {
        return "remove";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("User permissions remove sub command", "permissions.user.remove", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("user", null, ArgumentType.REQUIRED,
                Set.of(Argument.of("permission", "Adds the given permission to the user", ArgumentType.REQUIRED))));
    }
}
