/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagRawSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String tagName = context.getArg(0).toLowerCase();

        if (!context.getData().getManagement().hasTag(tagName)) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", tagName));
            return;
        }

        Tag tag = context.getData().getManagement().getTag(tagName);
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
    public CascadePermission getPermission() {
        return CascadePermission.of("tag.raw", false);
    }

}
