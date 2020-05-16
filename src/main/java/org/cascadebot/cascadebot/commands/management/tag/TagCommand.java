/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

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
            context.getUiMessaging().replyUsage();
            return;
        }

        String tagName = context.getArg(0).toLowerCase();

        if (!context.getData().getManagement().hasTag(context.getArg(0))) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", tagName));
            return;
        }

        Tag tag = context.getData().getManagement().getTag(tagName);
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