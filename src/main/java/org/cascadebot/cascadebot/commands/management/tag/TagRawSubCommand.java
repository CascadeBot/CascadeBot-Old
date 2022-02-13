/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTagEntity;
import org.cascadebot.cascadebot.data.entities.GuildTagId;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagRawSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String tagName = context.getArg(0).toLowerCase();
        GuildTagEntity tag = context.getDataObject(GuildTagEntity.class, new GuildTagId(context.getGuildId(), tagName));

        if (tag == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", tagName));
            return;
        }

        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(context.i18n("words.tag") + ": " + tagName);
        builder.setDescription("```" + tag.getContent() + "```");
        builder.addField(context.i18n("words.category"), tag.getCategory(), true);

        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "raw";
    }

    @Override
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("tag.raw", false);
    }

}
