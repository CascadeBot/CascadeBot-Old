/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.module;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class ModuleCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUiMessaging().replyUsage();
    }

    @Override
    public String command() {
        return "module";
    }

    @Override
    public Module module() {
        return Module.MANAGEMENT;
    }

    @Override
    public Set<DeprecatedSubCommand> subCommands() {
        return Set.of(new ModuleDisableSubCommand(), new ModuleEnableSubCommand(), new ModuleListSubCommand());
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("module", false, Permission.MANAGE_SERVER);
    }

}
