/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GroupPermissionCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUiMessaging().replyUsage();
    }

    @Override
    public Module module() {
        return Module.MANAGEMENT;
    }

    @Override
    public String command() {
        return "groupperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.group", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Set<SubCommand> subCommands() {
        return Set.of(new GroupPermissionCreateSubCommand(), new GroupPermissionDeleteSubCommand(), new GroupPermissionAddSubCommand(), new GroupPermissionRemoveSubCommand(),
                new GroupPermissionLinkRoleSubCommand(), new GroupPermissionUnlinkRoleSubCommand(), new GroupPermissionMoveSubCommand(),
                new GroupPermissionSwitchSubCommand(), new GroupPermissionListSubCommand(), new GroupPermissionInfoSubCommand());
    }

    @Override
    public List<Page> additionalUsagePages() {
        List<Page> extraPages = new ArrayList<>();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Test");
        builder.setDescription("This is a test embed");
        extraPages.add(new PageObjects.EmbedPage(builder));
        return extraPages;
    }
}
