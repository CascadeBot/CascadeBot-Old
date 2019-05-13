/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.subcommands.tag;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Map;
import java.util.Set;

public class TagRawSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this, "tag");
            return;
        }

        if (context.getData().hasTag(context.getArg(0))) {
            context.getTypedMessaging().replyDanger("Couldn't find tag with name `" + context.getArg(0) + "`");
            return;
        }

        Tag tag = context.getData().getTag(context.getArg(0));
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle("Tag: " + context.getArg(0));
        builder.setDescription("```" + tag.getContent() + "```");
        builder.addField("Category", tag.getCategory(), true);

        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "raw";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Tag raw sub command", "tag.raw", false);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("name", "Views the raw tag data for a given tag", ArgumentType.REQUIRED));
    }

}
