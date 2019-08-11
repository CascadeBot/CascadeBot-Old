/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class TagCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        if (!context.getCoreSettings().hasTag(context.getArg(0))) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", context.getArg(0)));
            return;
        }

        Tag tag = context.getCoreSettings().getTag(context.getArg(0));
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
    public Set<ISubCommand> getSubCommands() {
        return Set.of(new TagCreateSubCommand(), new TagDeleteSubCommand(), new TagListSubCommand(), new TagRawSubCommand(), new TagPlaceholdersSubCommand(),
                new TagCategorySubCommand(), new TagEditSubCommand());
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("tag", false);
    }

}
