/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.GuildPermissions;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupPermissionListSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getData().getPermissionSettings().getGroups().isEmpty()) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.list.no_groups"));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < context.getData().getPermissionSettings().getGroups().size(); i++) {
            Group group = context.getData().getPermissionSettings().getGroups().get(i);
            if (context.getData().getPermissionSettings().getMode().equals(GuildPermissions.PermissionMode.HIERARCHICAL)) {
                stringBuilder.append(i).append(": ");
            }
            stringBuilder.append(group.getName()).append(" (").append(group.getId()).append(")\n");
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
    public CascadePermission getPermission() {
        return CascadePermission.of("permissions.group.list", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
