/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildTagEntity;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DatabaseUtilsKt;

import java.util.List;
import java.util.stream.Collectors;

public class TagListSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {

        List<GuildTagEntity> tags = context.transaction(session -> {
            return DatabaseUtilsKt.listOf(session, GuildTagEntity.class, "guild_id", context.getGuildId());
        });

        if (tags == null || tags.size() == 0) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.tag.list.no_tags_found"));
            return;
        }

        String tagsList = tags.stream()
                .map(GuildTagEntity::getName)
                .collect(Collectors.joining("\n"));

        context.reply(tagsList);
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
