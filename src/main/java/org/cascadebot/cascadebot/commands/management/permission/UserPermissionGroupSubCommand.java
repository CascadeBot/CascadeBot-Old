/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
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

        if (!context.testForArg("put") && !context.testForArg("remove")) {
            context.getUIMessaging().replyUsage();
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(1));
        if (member == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.permission_not_exist", context.getArg(1)));
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(2), group -> {
            User user = context.getData().getPermissions().getPermissionUser(member);
            if (context.testForArg("put")) {
                if (user.addGroup(group)) {
                    context.getTypedMessaging().replySuccess(context.i18n("commands.userperms.group.put.success", member.getUser().getAsTag(), group.getName()));
                } else {
                    context.getTypedMessaging().replyWarning(context.i18n("commands.userperms.group.put.fail", member.getUser().getAsTag(), group.getName()));
                }
            } else if (context.testForArg("remove")) {
                if (user.removeGroup(group)) {
                    context.getTypedMessaging().replySuccess(context.i18n("commands.userperms.group.remove.success", member.getUser().getAsTag(), group.getName()));
                } else {
                    context.getTypedMessaging().replyWarning(context.i18n("commands.userperms.group.remove.fail", member.getUser().getAsTag(), group.getName()));
                }
            }
        }, sender.getIdLong());


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
