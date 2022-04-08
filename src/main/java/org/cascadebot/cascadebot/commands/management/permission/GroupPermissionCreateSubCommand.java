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
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DatabaseUtilsKt;

public class GroupPermissionCreateSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Integer amount = context.transaction(session -> {
            return DatabaseUtilsKt.count(session, GuildPermissionGroupEntity.class, "guild_id", context.getGuildId());
        });

        if (amount == null) {
            throw new UnsupportedOperationException("Count returned null in group create. This shouldn't happen!");
        }

        GuildPermissionGroupEntity group = new GuildPermissionGroupEntity(context.getArg(0), context.getGuildId());
        group.setPosition(amount);
        context.saveDataObject(group);
        context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.create.success", context.getArg(0)));
    }

    @Override
    public String command() {
        return "create";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.group.create", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
