/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.ModlogEventStore;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.moderation.ModlogEmbedField;
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.ArrayList;
import java.util.List;

public class TagEditSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        String tagName = context.getArg(0).toLowerCase();

        Tag tag = context.getData().getManagement().getTag(tagName);
        if (tag == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.cannot_find_tag", tagName));
            return;
        }
        String oldContent = tag.getContent();
        tag.setContent(context.getMessage(1));
        context.getTypedMessaging().replySuccess(context.i18n("commands.tag.edit.successfully_edited_tag", tagName));
        ModlogEvent event = ModlogEvent.CASCADE_TAG_UPDATED;
        List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
        embedFieldList.add(new ModlogEmbedField(false, "modlog.tag.old_content", "modlog.general.variable", "```" + MarkdownSanitizer.sanitize(oldContent) + "```"));
        embedFieldList.add(new ModlogEmbedField(false, "modlog.tag.new_content", "modlog.general.variable", "```" + MarkdownSanitizer.sanitize(tag.getContent()) + "```"));
        ModlogEventStore eventStore = new ModlogEventStore(event, sender.getUser(), tag, embedFieldList);
        context.getData().getModeration().sendModlogEvent(context.getGuild().getIdLong(), eventStore);
    }

    @Override
    public String command() {
        return "edit";
    }

    @Override
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("tag.edit", false);
    }

}
