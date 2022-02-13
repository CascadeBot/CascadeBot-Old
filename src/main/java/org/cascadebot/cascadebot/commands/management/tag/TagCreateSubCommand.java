/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import kotlin.jvm.functions.Function1;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTagEntity;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.hibernate.Session;

public class TagCreateSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        // Warn if the original argument contains uppercase letters
        boolean warnUppercase = !context.getArg(0).equals(context.getArg(0).toLowerCase());
        String tagName = context.getArg(0).toLowerCase();

        if (context.getData().getManagement().hasTag(tagName)) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.create.tag_already_exists", tagName));
            return;
        }

        String message = context.i18n("commands.tag.create.successfully_created_tag", tagName);

        if (warnUppercase) {
            message += "\n\n" + context.i18n("commands.tag.create.warn_uppercase");
        }

        GuildTagEntity guildTagEntity = new GuildTagEntity(context.getGuild().getIdLong(), context.getArg(0), context.getMessage(1));
        context.saveDataObject(guildTagEntity);
        context.getData().getPermissionsManager().registerGuildPermission(guildTagEntity.getInternalPermission());
        context.getTypedMessaging().replySuccess(message);
    }

    @Override
    public String command() {
        return "create";
    }

    @Override
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("tag.create", false);
    }

}
