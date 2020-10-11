/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.module;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ExtensionsKt;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModuleListSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String moduleList = Arrays.stream(Module.values())
                .filter(module -> !module.isPrivate())
                .map(module -> (context.getData().getCore().isModuleEnabled(module) ? context.globalEmote("tick") : context.globalEmote("cross")) +
                        ExtensionsKt.toCapitalized(FormatUtils.formatEnum(module, context.getLocale())))
                .collect(Collectors.joining("\n"));

        context.getTypedMessaging().replyInfo(
                MessagingObjects.getStandardMessageEmbed(
                    moduleList,
                    context.getUser()).setTitle(ExtensionsKt.toCapitalized(context.i18n("words.modules"))
                )
        );
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
    public CascadePermission permission() {
        return CascadePermission.of("module.list", false, Permission.MANAGE_SERVER);
    }

}
