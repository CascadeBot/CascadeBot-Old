/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.subcommands.module;

import com.cascadebot.cascadebot.commandmeta.Argument;
import com.cascadebot.cascadebot.commandmeta.ArgumentType;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;

import java.util.Set;

public class ModuleEnableSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if(context.getArgs().length < 1) {
            context.replyUsage(this, "module");
            return;
        }
        String selectedModule = context.getArg(0).toUpperCase();
        Module module = EnumUtils.getEnum(Module.class, selectedModule);

        if (module != null) {
            if (context.getData().isModuleEnabled(module)) {
                context.replyInfo("The module `%s` is already enabled!", module.toString());
                return;
            }
            try {
                context.getData().enableModule(module);
                context.replySuccess("The module `%s` has been enabled!", module.toString());
            } catch (IllegalArgumentException ex) {
                context.replyDanger(ex.getMessage());
            }
        } else {
            context.replyDanger("We couldn't find that module. Use `" + "" + "module list` for a list of modules.");
        }

    }

    @Override
    public String command() {
        return "enable";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Enable module subcommand", "module.enable", false, Permission.MANAGE_SERVER);
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("module", "Enables a module", ArgumentType.REQUIRED));
    }

}
