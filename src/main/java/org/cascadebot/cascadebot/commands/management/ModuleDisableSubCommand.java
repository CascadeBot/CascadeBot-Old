/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class ModuleDisableSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }
        String selectedModule = context.getArg(0).toUpperCase();
        Module module = EnumUtils.getEnum(Module.class, selectedModule);

        if (module != null) {
            try {
                if (context.getData().getCoreSettings().disableModule(module)) {
                    // If module wasn't already disabled
                    context.getTypedMessaging().replySuccess(context.i18n("commands.module.disable.disabled", module.toString()));
                } else {
                    // If module was already disabled
                    context.getTypedMessaging().replyInfo(context.i18n("commands.module.disable.already_disabled", module.toString()));
                }
            } catch (IllegalArgumentException ex) {
                context.getTypedMessaging().replyDanger(ex.getMessage());
            }
        } else {
            context.getTypedMessaging().replyDanger(context.i18n("commands.module.disable.cannot_find_module"));
        }

    }

    @Override
    public String command() {
        return "disable";
    }

    @Override
    public String parent() {
        return "module";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("module.disable", false, Permission.MANAGE_SERVER);
    }

}
