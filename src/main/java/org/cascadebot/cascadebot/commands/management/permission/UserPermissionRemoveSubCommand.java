/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildPermissionUserEntity;
import org.cascadebot.cascadebot.data.entities.GuildPermissionUserId;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class UserPermissionRemoveSubCommand extends SubCommand {

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

        GuildPermissionUserEntity user = context.getDataObject(GuildPermissionUserEntity.class, new GuildPermissionUserId(member.getIdLong(), context.getGuildId()));
        if (user == null) {
            user = new GuildPermissionUserEntity(member.getIdLong(), context.getGuildId());
        }

        if (user.getPermissions().remove(context.getArg(1))) {
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
    public CascadePermission permission() {
        return CascadePermission.of("permissions.user.remove", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
