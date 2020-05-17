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

public class UserPermissionRemoveSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (member == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.permission_not_exist", context.getArg(1)));
            return;
        }

        User user = context.getData().getManagement().getPermissions().getPermissionUser(member);

        if (user.removePermission(context.getArg(1))) {
            context.getTypedMessaging().replySuccess(context.i18n("commands.userperms.remove.success", context.getArg(1), member.getUser().getAsTag()));
        } else {
            context.getTypedMessaging().replyWarning(context.i18n("commands.userperms.remove.fail", context.getArg(1), member.getUser().getAsTag()));
        }
    }

    @Override
    public String command() {
        return "remove";
    }

    @Override
    public String parent() {
        return "userperms";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("permissions.user.remove", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
