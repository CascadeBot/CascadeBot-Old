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
import org.cascadebot.cascadebot.data.objects.ModlogEventStore;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.LanguageEmbedField;

import java.util.ArrayList;
import java.util.List;

public class ModuleDisableSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }
        String selectedModule = context.getArg(0).toUpperCase();
        Module module = EnumUtils.getEnum(Module.class, selectedModule);

        if (module != null) {
            try {
                if (context.getData().getCore().disableModule(module)) {
                    // If module wasn't already disabled
                    context.getTypedMessaging().replySuccess(context.i18n("commands.module.disable.disabled", module.toString()));
                    ModlogEvent event = ModlogEvent.CASCADE_MODULE_UPDATED;
                    List<LanguageEmbedField> embedFieldList = new ArrayList<>();
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.module.enabled", "modlog.general.variable", "false"));
                    ModlogEventStore eventStore = new ModlogEventStore(event, sender.getUser(), module, embedFieldList);
                    context.getData().getModeration().sendModlogEvent(context.getGuild().getIdLong(), eventStore);
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
    public CascadePermission permission() {
        return CascadePermission.of("module.disable", false, Permission.MANAGE_SERVER);
    }

}
