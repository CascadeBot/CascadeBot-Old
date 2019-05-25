/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import java.util.Set;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagCategorySubCommand;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagCreateSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagDeleteSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagEditSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagListSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagPlaceholdersSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagRawSubCommand;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this);
            return;
        }

        if (!context.getSettings().hasTag(context.getArg(0))) {
            context.getTypedMessaging().replyDanger("Couldn't find tag with name `" + context.getArg(0) + "`");
            return;
        }

        Tag tag = context.getSettings().getTag(context.getArg(0));
        context.reply(tag.formatTag(context));
    }

    @Override
    public Module getModule() {
        return Module.MANAGEMENT;
    }

    @Override
    public String command() {
        return "tag";
    }

    @Override
    public Set<ICommandExecutable> getSubCommands() {
        return Set.of(new TagCreateSubCommand(), new TagDeleteSubCommand(), new TagListSubCommand(), new TagRawSubCommand(), new TagPlaceholdersSubCommand(),
                new TagCategorySubCommand(), new TagEditSubCommand());
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Tag command", "tag", false);
    }

    @Override
    public String description() {
        return "Tag command";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("name", "Display the given tag", ArgumentType.REQUIRED));
    }

}
