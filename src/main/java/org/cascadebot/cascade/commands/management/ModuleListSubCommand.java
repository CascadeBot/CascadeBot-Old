/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.management;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandExecutable;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.commandmeta.ModuleFlag;
import org.cascadebot.cascade.permissions.CascadePermission;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModuleListSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply("**Modules**\n" + Arrays.stream(Module.values())
                .filter(module -> !module.isFlagEnabled(ModuleFlag.PRIVATE))
                .map(module -> module.toString().toLowerCase() +
                        " - " +
                        (context.getSettings().isModuleEnabled(module) ? "Enabled" : "Disabled"))
                .collect(Collectors.joining("\n")));
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("List modules subcommand", "module.list", false, Permission.MANAGE_SERVER);
    }

    @Override
    public String description() {
        return "Lists all modules";
    }

}
