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

public class TagCreateSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "tag");
            return;
        }
        context.getSettings().addTag(context.getArg(0), new Tag(context.getMessage(1), "tag"));
        context.getTypedMessaging().replySuccess("Successfully created tag with name `%s`", context.getArg(0));
    }

    @Override
    public String command() {
        return "create";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Tag create sub command", "tag.create", false);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("name", null, ArgumentType.REQUIRED, Set.of(Argument.of("tag", "Creates a tag with a giving name", ArgumentType.REQUIRED))));
    }

}
