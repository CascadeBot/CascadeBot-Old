/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagCategorySubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage();
            return;
        }

        Tag tag = context.getCoreSettings().getTag(context.getArg(0));
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
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("tag.category", false);
    }

}
