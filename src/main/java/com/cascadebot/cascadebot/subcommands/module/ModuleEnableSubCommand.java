/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.subcommands.module;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;

public class ModuleEnableSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String selectedModule = context.getArg(0).toUpperCase();
        Module module = EnumUtils.getEnum(Module.class, selectedModule);

        if (module != null) {
            if (context.getData().isModuleEnabled(module)) {
                context.replyInfo("The module `%s` is already enabled!", module.toString());
                return;
            }
            try {
                context.getData().enableModule(module);
                context.replySuccess("Module `%s` has been enabled!", module.toString());
            } catch (IllegalArgumentException ex) {
                context.replyDanger(ex.getMessage());
            }
        } else {
            context.replyDanger("We couldn't find that module. Use `" + context.getData().getCommandPrefix() + "module list` for a list of modules.");
        }

    }

    @Override
    public String command() {
        return "enable";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("core.module.enable");
    }

}
