/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagCategorySubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        context.getData().write(guildData -> {
            Tag tag = context.getData().getManagement().getTag(context.getArg(0));

            String tagName = context.getArg(0).toLowerCase();
            String category = context.getArg(1).toLowerCase();

            if (tag == null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", tagName));
                return;
            }

            tag.setCategory(category);
            context.getTypedMessaging().replySuccess(context.i18n("commands.tag.category.successfully_set_tag", tagName, category));
        });
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
    public CascadePermission permission() {
        return CascadePermission.of("tag.category", false);
    }

}
