/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class TagCategorySubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "tag");
            return;
        }

        Tag tag = context.getSettings().getTag(context.getArg(0));
        if (tag == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", context.getArg(0)));
            return;
        }

        tag.setCategory(context.getArg(1));
        context.getTypedMessaging().replySuccess(context.i18n("commands.tag.category.successfully_set_tag", context.getArg(0), context.getArg(1)));
    }

    @Override
    public String command() {
        return "category";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Tag category sub command", "tag.category", false);
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("tag", null, ArgumentType.REQUIRED,
                Set.of(Argument.of("category", "Sets the category for the tag to go into", ArgumentType.REQUIRED))));
    }

    @Override
    public String description() {
        return null;
    }

}
