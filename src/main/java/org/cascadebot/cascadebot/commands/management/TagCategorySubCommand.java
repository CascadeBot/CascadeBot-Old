/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.guild.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagCategorySubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Tag tag = context.getCoreSettings().getTags().get(context.getArg(0));

        String tagName = context.getArg(0).toLowerCase();
        String category = context.getArg(1).toLowerCase();

        if (tag == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", tagName));
            return;
        }

        tag.setCategory(category);
        context.getTypedMessaging().replySuccess(context.i18n("commands.tag.category.successfully_set_tag", tagName, category));
    }

    @Override
    public String command() {
        return "category";
    }

    @Override
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("tag.category", false);
    }

}
