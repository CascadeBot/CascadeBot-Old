/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupEntity;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupId;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;

public class GroupPermissionRemoveSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        if (!CascadeBot.INS.getPermissionsManager().isValidPermission(context.getArg(1))) { // TODO guild level checking
            context.getTypedMessaging().replyDanger(context.i18n("responses.permission_not_exist", context.getArg(1)));
            return;
        }

        GuildPermissionGroupEntity group = context.getDataObject(GuildPermissionGroupEntity.class, new GuildPermissionGroupId(context.getArg(0), context.getGuildId()));

        if (group == null) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.no_group", context.getArg(0)));
            return;
        }

        if (group.getPermissions().remove(context.getArg(1))) {
            context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.remove.success", context.getArg(1), group.getName()));
            context.saveDataObject(group);
        } else {
            context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.remove.fail", context.getArg(1), group.getName()));
        }
    }

    @Override
    public String command() {
        return "remove";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.group.remove", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
