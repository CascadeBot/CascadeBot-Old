/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.subcommands.module;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class ModuleEnableSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if(context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this, "module");
            return;
        }
        String selectedModule = context.getArg(0).toUpperCase();
        Module module = EnumUtils.getEnum(Module.class, selectedModule);

        if (module != null) {
            try {
                if (context.getData().enableModule(module)) {
                    // If the module wasn't enabled
                    context.getTypedMessaging().replySuccess(context.i18n("commands.module.enable.enabled", module.toString()));
                } else {
                    // If the module was enabled
                    context.getTypedMessaging().replyInfo(context.i18n("commands.module.enable.already_enabled", module.toString()));
                }
            } catch (IllegalArgumentException ex) {
                context.getTypedMessaging().replyDanger(ex.getMessage());
            }
        } else {
            context.getTypedMessaging().replyDanger(context.i18n("commands.module.enable.cannot_find_module"));
        }

    }

    @Override
    public String command() {
        return "enable";
    }

    @Override
    public String parent() {
        return "module";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Enable module subcommand", "module.enable", false, Permission.MANAGE_SERVER);
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("module", "Enables a module", ArgumentType.REQUIRED));
    }

}
