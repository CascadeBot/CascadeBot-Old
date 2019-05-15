/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.subcommands.tag;

import java.util.Set;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagEditSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if(context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "tag");
            return;
        }

        Tag tag = context.getData().getTag(context.getArg(0));
        if(tag == null) {
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
