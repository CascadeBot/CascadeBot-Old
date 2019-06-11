/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.management;

import java.util.Set;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.Argument;
import org.cascadebot.cascade.commandmeta.ArgumentType;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandExecutable;
import org.cascadebot.cascade.data.objects.Tag;
import org.cascadebot.cascade.permissions.CascadePermission;

public class TagEditSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "tag");
            return;
        }

        Tag tag = context.getSettings().getTag(context.getArg(0));
        if (tag == null) {
            context.getTypedMessaging().replyDanger("Tag `" + context.getArg(0) + "` not found");
            return;
        }

        tag.setContent(context.getMessage(1));
        context.getTypedMessaging().replySuccess("Updated tag `" + context.getArg(0) + "`");
    }

    @Override
    public String command() {
        return "edit";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Tag edit sub command", "tag.edit", false);
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("tag", null, ArgumentType.REQUIRED,
                Set.of(Argument.of("content", "Edits the specified tag", ArgumentType.REQUIRED))));
    }

    @Override
    public String description() {
        return null;
    }

}
