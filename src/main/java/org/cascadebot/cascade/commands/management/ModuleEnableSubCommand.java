/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.management;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascade.commandmeta.Argument;
import org.cascadebot.cascade.commandmeta.ArgumentType;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandExecutable;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.permissions.CascadePermission;

import java.util.Set;

public class ModuleEnableSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this, "module");
            return;
        }
        String selectedModule = context.getArg(0).toUpperCase();
        Module module = EnumUtils.getEnum(Module.class, selectedModule);

        if (module != null) {
            try {
                if (context.getSettings().enableModule(module)) {
                    // If the module wasn't enabled
                    context.getTypedMessaging().replySuccess("The module `%s` has been enabled!", module.toString());
                } else {
                    // If the module was enabled
                    context.getTypedMessaging().replyInfo("The module `%s` is already enabled!", module.toString());
                }
            } catch (IllegalArgumentException ex) {
                context.getTypedMessaging().replyDanger(ex.getMessage());
            }
        } else {
            context.getTypedMessaging().replyDanger("We couldn't find that module. Use `" + "" + "module list` for a list of modules.");
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
