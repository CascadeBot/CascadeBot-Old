/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.ModlogEventData;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Group;

import java.util.ArrayList;

public class GroupPermissionCreateSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        context.getData().write(guildData -> {
            Group group = guildData.getManagement().getPermissions().createGroup(context.getArg(0));
            context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.create.success", context.getArg(0), group.getId()));
            ModlogEvent event = ModlogEvent.CASCADE_PERMISSIONS_GROUP_CREATED;
            ModlogEventData eventStore = new ModlogEventData(event, sender.getUser(), group, new ArrayList<>());
            context.getData().getModeration().sendModlogEvent(context.getGuild().getIdLong(), eventStore);
        });
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
