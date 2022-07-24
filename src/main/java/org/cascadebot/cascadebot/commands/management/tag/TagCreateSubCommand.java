/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTagEntity;
import org.cascadebot.cascadebot.data.entities.GuildTagId;

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

        GuildTagEntity existing = context.getDataObject(GuildTagEntity.class, new GuildTagId(context.getGuildId(), tagName));
        if (existing != null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.create.tag_already_exists", tagName));
            return;
        }

        String message = context.i18n("commands.tag.create.successfully_created_tag", tagName);

        if (warnUppercase) {
            message += "\n\n" + context.i18n("commands.tag.create.warn_uppercase");
        }

        GuildTagEntity guildTagEntity = new GuildTagEntity(context.getGuild().getIdLong(), context.getArg(0), context.getMessage(1));
        context.saveDataObject(guildTagEntity);
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

}
