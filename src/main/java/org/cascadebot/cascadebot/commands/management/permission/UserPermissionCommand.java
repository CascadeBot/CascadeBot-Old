/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class UserPermissionCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUiMessaging().replyUsage();
    }

    @Override
    public String command() {
        return "userperms";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("permissions.user", false, Module.MANAGEMENT);
    }

    @Override
    public Set<ISubCommand> getSubCommands() {
        return Set.of(new UserPermissionAddSubCommand(), new UserPermissionRemoveSubCommand(), new UserPermissionGroupSubCommand(),
                new UserPermissionListSubCommand(), new UserPermissionTestSubCommand());
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Module getModule() {
        return Module.MANAGEMENT;
    }

}
