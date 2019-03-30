/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commands.subcommands.module.ModuleDisableSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.module.ModuleEnableSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.module.ModuleListSubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class ModuleCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUIMessaging().replyUsage(this);
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
    public Set<ICommandExecutable> getSubCommands() {
        return Set.of(new ModuleDisableSubCommand(), new ModuleEnableSubCommand(), new ModuleListSubCommand());
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Module command", "module", false, Permission.MANAGE_SERVER);
    }

    @Override
    public String description() {
        return "Interacts with modules by enabling, disabling or listing them";
    }

}
