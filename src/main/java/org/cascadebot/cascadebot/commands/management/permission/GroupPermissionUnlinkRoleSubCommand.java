/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupEntity;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupId;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;

public class GroupPermissionUnlinkRoleSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Role role = DiscordUtils.getRole(context.getArg(1), context.getGuild());
        if (role == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.cannot_find_role_matching", context.getArg(1)));
            return;
        }

        GuildPermissionGroupEntity group = context.getDataObject(GuildPermissionGroupEntity.class, new GuildPermissionGroupId(context.getArg(0), context.getGuildId()));

        if (group == null) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.no_group", context.getArg(0)));
            return;
        }

        if (group.getRoles().remove(role.getIdLong())) {
            context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.unlink.success", group.getName(), role.getName()));
        } else {
            context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.unlink.fail", group.getName(), role.getName()));
        }
    }

    @Override
    public String command() {
        return "unlink";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.group.unlink", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
