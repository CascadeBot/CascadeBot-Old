/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class UserPermissionCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUiMessaging().replyUsage();
    }

    @Override
    public String command() {
        return "userperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.user", false, Module.MANAGEMENT);
    }

    @Override
    public Set<SubCommand> subCommands() {
        return Set.of(new UserPermissionAddSubCommand(), new UserPermissionRemoveSubCommand(), new UserPermissionGroupSubCommand(),
                new UserPermissionListSubCommand(), new UserPermissionTestSubCommand());
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Module module() {
        return Module.MANAGEMENT;
    }

}
