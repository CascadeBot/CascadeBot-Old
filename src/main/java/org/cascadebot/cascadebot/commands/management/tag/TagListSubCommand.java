/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Map;

public class TagListSubCommand extends DeprecatedSubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Map<String, Tag> tags = context.getData().getManagement().getTags();

        if (tags.size() == 0) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.tag.list.no_tags_found"));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Tag> tagEntry : tags.entrySet()) {
            builder.append(tagEntry.getKey()).append('\n');
        }
        context.reply(builder.toString());
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("tag.list", false);
    }

}
