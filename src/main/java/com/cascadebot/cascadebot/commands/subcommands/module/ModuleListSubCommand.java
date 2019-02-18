/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.subcommands.module;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModuleListSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply("**Modules**\n" + Arrays.stream(Module.values())
                .map(module -> module.toString().toLowerCase() +
                        " - " +
                        (context.getData().isModuleEnabled(module) ? "Enabled" : "Disabled"))
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

}
