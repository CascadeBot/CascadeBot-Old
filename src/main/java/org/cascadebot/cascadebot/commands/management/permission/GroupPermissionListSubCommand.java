/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.GuildPermissions;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

public class GroupPermissionListSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getData().getPermissions().getGroups().isEmpty()) {
            context.getTypedMessaging().replyWarning("You have no groups!");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (Group group : context.getData().getPermissions().getGroups()) {
            if (context.getData().getPermissions().getMode().equals(GuildPermissions.PermissionMode.HIERARCHICAL)) {
                stringBuilder.append(i).append(": ");
            }
            stringBuilder.append(group.getName()).append(" (").append(group.getId()).append(")\n");
            i++;
        }

        List<String> stingPages = PageUtils.splitString(stringBuilder.toString(), 1000, '\n');
        List<Page> pages = new ArrayList<>();
        for (String pageContent : stingPages) {
            pages.add(new PageObjects.StringPage(pageContent));
        }

        context.getUIMessaging().sendPagedMessage(pages);
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Group permissions list sub command", "permissions.group.list", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
