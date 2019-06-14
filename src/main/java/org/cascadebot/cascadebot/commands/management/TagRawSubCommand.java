/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class TagRawSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        if (!context.getSettings().hasTag(context.getArg(0))) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", context.getArg(0)));
            return;
        }

        Tag tag = context.getSettings().getTag(context.getArg(0));
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(context.i18n("words.tag") + ": " + context.getArg(0));
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

    @Override
    public String description() {
        return null;
    }

}
