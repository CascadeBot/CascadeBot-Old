/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModuleListSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply("**Modules**\n" + Arrays.stream(Module.values())
                .filter(module -> !module.isPrivate())
                .map(module -> module.toString().toLowerCase() +
                        " - " +
                        (context.getCoreSettings().isModuleEnabled(module) ? "Enabled" : "Disabled"))
                .collect(Collectors.joining("\n")));
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public String parent() {
        return "module";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("module.list", false, Permission.MANAGE_SERVER);
    }

}
