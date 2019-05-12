/*

  * Copyright (c) 2019 CascadeBot. All rights reserved.
  * Licensed under the MIT license.

 */

package org.cascadebot.cascadebot.commands.subcommands.tag;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Map;

public class TagListSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Map<String, Tag> tags = context.getData().getTagInfo();
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, Tag> tagEntry : tags.entrySet()) {
            builder.append(tagEntry.getKey()).append('\n');
        }
        context.reply(builder.toString());
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Tag list sub command", "tag.list", false);
    }

    @Override
    public String description() {
        return "Lists all tags";
    }

}
