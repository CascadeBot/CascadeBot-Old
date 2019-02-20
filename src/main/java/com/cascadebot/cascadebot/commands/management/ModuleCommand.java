/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.management;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.commands.subcommands.module.ModuleDisableSubCommand;
import com.cascadebot.cascadebot.commands.subcommands.module.ModuleEnableSubCommand;
import com.cascadebot.cascadebot.commands.subcommands.module.ModuleListSubCommand;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;

public class ModuleCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.replyInfo("Use subcommands disable, enable and list!");
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
        return "interact with modules";
    }

}
