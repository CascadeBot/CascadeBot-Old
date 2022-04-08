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

import java.util.Map;

public class TagDeleteSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String tagName = context.getArg(0).toLowerCase();

        Integer deletedNum = context.transaction(session -> {
            return DatabaseUtilsKt.deleteById(
                    session,
                    GuildTagEntity.class,
                    Map.of("name", tagName, "guild_id", context.getGuildId())
            );
        });

        if (deletedNum != null && deletedNum > 0) {
            context.getTypedMessaging().replySuccess(context.i18n("commands.tag.delete.successfully_deleted_tag"));
        } else {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.delete.tag_doesnt_exist", tagName));
        }
    }

    @Override
    public String command() {
        return "delete";
    }

    @Override
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("tag.delete", false);
    }

}
