/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class ModuleCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUIMessaging().replyUsage();
    }

    @Override
    public String command() {
        return "module";
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

    @Override
    public Module getModule() {
        return Module.MANAGEMENT;
    }

    @Override
    public Set<ISubCommand> getSubCommands() {
        return Set.of(new ModuleDisableSubCommand(), new ModuleEnableSubCommand(), new ModuleListSubCommand());
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("module", false, Permission.MANAGE_SERVER);
    }

}
