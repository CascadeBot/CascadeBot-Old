/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.module;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildModuleEntity;
import org.cascadebot.cascadebot.utils.ExtensionsKt;
import org.cascadebot.cascadebot.utils.FormatUtils;

public class ModuleEnableSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }
        String selectedModule = context.getArg(0).toUpperCase();
        GuildModuleEntity guildModuleEntity = context.getDataObject(GuildModuleEntity.class);

        if (guildModuleEntity == null) {
            guildModuleEntity = new GuildModuleEntity();
        }

        Module module = EnumUtils.getEnum(Module.class, selectedModule);

        if (module == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.module.cannot_find_module"));
            return;
        }

        String moduleName = ExtensionsKt.toCapitalized(FormatUtils.formatEnum(module, context.getLocale()));

        boolean moduleEnabled = guildModuleEntity.getModuleEnabled(module);

        if (moduleEnabled) {
            // If module was already disabled
            context.getTypedMessaging().replyInfo(context.i18n("commands.module.enable.already_enabled", moduleName));
            return;
        }

        guildModuleEntity.setModuleEnabled(module, true);

        context.saveDataObject(guildModuleEntity);
        context.getTypedMessaging().replySuccess(context.i18n("commands.module.enable.enabled", moduleName));

    }

    @Override
    public String command() {
        return "enable";
    }

    @Override
    public String parent() {
        return "module";
    }

}
