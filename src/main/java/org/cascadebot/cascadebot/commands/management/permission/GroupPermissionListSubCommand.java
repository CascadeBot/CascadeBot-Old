/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupEntity;
import org.cascadebot.cascadebot.data.entities.GuildSettingsManagementEntity;
import org.cascadebot.cascadebot.data.objects.PermissionMode;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DatabaseUtilsKt;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupPermissionListSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        List<GuildPermissionGroupEntity> groupEntities = context.transaction(session -> {
            return DatabaseUtilsKt.listOf(session, GuildPermissionGroupEntity.class, "guild_id", context.getGuildId());
        });
        if (groupEntities == null) {
            throw new UnsupportedOperationException("TODO"); //TODO message
        }
        if (groupEntities.size() == 0) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.list.no_groups"));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        groupEntities.sort(GuildPermissionGroupEntity::compareTo);
        for (GuildPermissionGroupEntity group : groupEntities) {
            GuildSettingsManagementEntity guildSettingsManagement = context.getDataObject(GuildSettingsManagementEntity.class);
            if (guildSettingsManagement == null) {
                throw new UnsupportedOperationException("TODO"); // TODO message
            }
            if (guildSettingsManagement.getPermissionMode().equals(PermissionMode.HIERARCHICAL)) {
                stringBuilder.append(group.getPosition()).append(": ");
            }
            stringBuilder.append(group.getName()).append("\n");
        }

        List<String> stringPages = PageUtils.splitString(stringBuilder.toString(), 1000, '\n');
        List<Page> pages = new ArrayList<>();
        for (String pageContent : stringPages) {
            pages.add(new PageObjects.EmbedPage(new EmbedBuilder().setDescription(pageContent)));
        }

        context.getUiMessaging().sendPagedMessage(pages);
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.group.list", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
