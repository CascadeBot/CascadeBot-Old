/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.module;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildModuleEntity;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ExtensionsKt;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.lang.reflect.InvocationTargetException;

public class ModuleEnableSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }
        String selectedModule = context.getArg(0).toUpperCase();
        GuildModuleEntity guildModuleEntity = context.getDataObject(GuildModuleEntity.class);
        Module module = EnumUtils.getEnum(Module.class, selectedModule);

        if (module != null) {
            // TODO should we do this? It's very spaghet, but it makes is so we don't have to write an if or switch statement thats subject to a lot of change
            String moduleGetMethod = "get" + ExtensionsKt.toCapitalized(module.name());
            String moduleMethod = "set" + ExtensionsKt.toCapitalized(module.name());
            String moduleName = ExtensionsKt.toCapitalized(FormatUtils.formatEnum(module, context.getLocale()));
            try {
                boolean enabled = (boolean) guildModuleEntity.getClass().getDeclaredMethod(moduleGetMethod).invoke(guildModuleEntity);
                if (enabled) {
                    // If module was already disabled
                    context.getTypedMessaging().replyInfo(context.i18n("commands.module.disable.already_disabled", moduleName));
                    return;
                }
                guildModuleEntity.getClass().getDeclaredMethod(moduleMethod, Boolean.class).invoke(guildModuleEntity, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                context.getTypedMessaging().replyException("Failed to enable module", e); // TODO language
                return;
            }

            context.saveDataObject(guildModuleEntity);
            context.getTypedMessaging().replySuccess(context.i18n("commands.module.enable.enable", moduleName));
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
    public CascadePermission permission() {
        return CascadePermission.of("module.enable", false, Permission.MANAGE_SERVER);
    }

}
