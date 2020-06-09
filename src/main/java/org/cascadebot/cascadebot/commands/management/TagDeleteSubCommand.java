/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.ModlogEventStore;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.LanguageEmbedField;

import java.util.ArrayList;
import java.util.List;

public class TagDeleteSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String tagName = context.getArg(0).toLowerCase();

        Tag tag = context.getData().getManagement().getTag(tagName);
        if (context.getData().getManagement().removeTag(tagName) && tag != null) {
            context.getTypedMessaging().replySuccess(context.i18n("commands.tag.delete.successfully_deleted_tag"));
            ModlogEvent event = ModlogEvent.CASCADE_TAG_DELETED;
            ModlogEventStore eventStore = new ModlogEventStore(event, sender.getUser(), tag, new ArrayList<>());
            context.getData().getModeration().sendModlogEvent(context.getGuild().getIdLong(), eventStore);
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
