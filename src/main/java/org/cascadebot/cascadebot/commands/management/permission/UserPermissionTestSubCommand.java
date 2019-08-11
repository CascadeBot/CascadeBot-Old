/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class UserPermissionTestSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage();
            return;
        }

        Member target = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (target == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.cannot_find_user_matching", context.getArg(0)));
            return;
        }

        CascadePermission perm = CascadeBot.INS.getPermissionsManager().getPermission(context.getArg(1));
        if (perm == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.permission_not_exist", context.getArg(1)));
            return;
        }

        if (context.getData().getPermissions().hasPermission(target, context.getChannel(), perm, context.getCoreSettings())) {
            context.getTypedMessaging().replyInfo(context.i18n("commands.userperms.test.has", target.getUser().getAsTag(), perm.getPermission(context.getLocale())));
        } else {
            context.getTypedMessaging().replyInfo(context.i18n("commands.userperms.test.does_not_have", target.getUser().getAsTag()));
        }
    }

    @Override
    public String command() {
        return "test";
    }

    @Override
    public String parent() {
        return "userperms";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("permissions.user.test", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
