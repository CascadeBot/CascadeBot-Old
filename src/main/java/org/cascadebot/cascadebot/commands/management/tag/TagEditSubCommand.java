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

public class TagEditSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String tagName = context.getArg(0).toLowerCase();

        GuildTagEntity tag = context.getDataObject(GuildTagEntity.class, new GuildTagId(context.getGuildId(), tagName));

        if (tag == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", tagName));
            return;
        }

        tag.setContent(context.getMessage(1));
        context.saveDataObject(tag);
        context.getTypedMessaging().replySuccess(context.i18n("commands.tag.edit.successfully_edited_tag", tagName));
    }

    @Override
    public String command() {
        return "edit";
    }

    @Override
    public String parent() {
        return "tag";
    }

}
