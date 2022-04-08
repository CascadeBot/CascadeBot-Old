/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupEntity;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupId;
import org.cascadebot.cascadebot.data.entities.GuildPermissionUserEntity;
import org.cascadebot.cascadebot.data.entities.GuildPermissionUserId;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class UserPermissionGroupSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 3) {
            context.getUiMessaging().replyUsage();
            return;
        }

        if (!context.testForArg("put") && !context.testForArg("remove")) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(1));
        if (member == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.permission_not_exist", context.getArg(1)));
            return;
        }

        GuildPermissionGroupEntity group = context.transaction(session -> {
            return session.get(GuildPermissionGroupEntity.class, new GuildPermissionGroupId(context.getArg(2), context.getGuildId()));
        });
        if (group == null) {
            context.reply("Group not found"); // TODO language
        }

        GuildPermissionUserEntity user = context.getDataObject(GuildPermissionUserEntity.class, new GuildPermissionUserId(member.getIdLong(), context.getGuildId()));
        if (user == null) {
            user = new GuildPermissionUserEntity(member.getIdLong(), context.getGuildId());
        }
        if (context.testForArg("put")) {
            if (user.getGroups().add(group)) {
                context.getTypedMessaging().replySuccess(context.i18n("commands.userperms.group.put.success", member.getUser().getAsTag(), group.getName()));
            } else {
                context.getTypedMessaging().replyWarning(context.i18n("commands.userperms.group.put.fail", member.getUser().getAsTag(), group.getName()));
            }
        } else if (context.testForArg("remove")) {
            if (user.getGroups().remove(group)) {
                context.getTypedMessaging().replySuccess(context.i18n("commands.userperms.group.remove.success", member.getUser().getAsTag(), group.getName()));
            } else {
                context.getTypedMessaging().replyWarning(context.i18n("commands.userperms.group.remove.fail", member.getUser().getAsTag(), group.getName()));
            }
        }

        context.saveDataObject(user);

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
    public CascadePermission permission() {
        return CascadePermission.of("permissions.user.group", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
