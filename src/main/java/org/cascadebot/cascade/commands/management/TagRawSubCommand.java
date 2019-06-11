/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.management;

import java.util.Set;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.Argument;
import org.cascadebot.cascade.commandmeta.ArgumentType;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandExecutable;
import org.cascadebot.cascade.data.objects.Tag;
import org.cascadebot.cascade.messaging.MessagingObjects;
import org.cascadebot.cascade.permissions.CascadePermission;

public class TagRawSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this, "tag");
            return;
        }

        if (!context.getSettings().hasTag(context.getArg(0))) {
            context.getTypedMessaging().replyDanger("Couldn't find tag with name `" + context.getArg(0) + "`");
            return;
        }

        Tag tag = context.getSettings().getTag(context.getArg(0));
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
