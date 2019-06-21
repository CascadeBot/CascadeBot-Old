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
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class UserPermissionAddSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage();
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (member == null) {
            context.getTypedMessaging().replyDanger("User `" + context.getArg(0) + "` not found");
            return;
        }

        User user = context.getData().getPermissions().getPermissionUser(member);

        if (!CascadeBot.INS.getPermissionsManager().isValidPermission(context.getGuild(), context.getArg(1))) {
            context.getTypedMessaging().replyDanger("`%s` isn't a valid permission", context.getArg(1));
            return;
        }

        if (user.addPermission(context.getArg(1))) {
            context.getTypedMessaging().replySuccess("Successfully added permission `%s` to user `%s`", context.getArg(1), member.getUser().getAsTag());
        } else {
            context.getTypedMessaging().replyWarning("Couldn't add permission `%s` to user `%s` as they already have the permission", context.getArg(1), member.getUser().getAsTag());
        }
    }

    @Override
    public String command() {
        return "add";
    }

    @Override
    public String parent() {
        return "userperms";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("User permissions add sub command", "permissions.user.add", false, Module.MANAGEMENT);
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
