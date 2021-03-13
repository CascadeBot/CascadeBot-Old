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

public class TagCreateSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        // Warn if the original argument contains uppercase letters
        boolean warnUppercase = !context.getArg(0).equals(context.getArg(0).toLowerCase());
        String tagName = context.getArg(0).toLowerCase();

        if (context.getData().getManagement().hasTag(tagName)) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.create.tag_already_exists", tagName));
            return;
        }

        String message = context.i18n("commands.tag.create.successfully_created_tag", tagName);

        if (warnUppercase) {
            message += "\n\n" + context.i18n("commands.tag.create.warn_uppercase");
        }

        Tag tag = new Tag(context.getArg(0), context.getMessage(1), "tag");
        context.getData().getManagement().addTag(context.getArg(0), tag);
        context.getData().getPermissionsManager().registerGuildPermission(tag.getInternalPermission());
        context.getTypedMessaging().replySuccess(message);
        ModlogEvent event = ModlogEvent.CASCADE_TAG_CREATED;
        List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
        embedFieldList.add(new ModlogEmbedField(false, "modlog.tag.content", "modlog.general.variable", "```" + MarkdownSanitizer.sanitize(tag.getContent()) + "```"));
        ModlogEventStore eventStore = new ModlogEventStore(event, sender.getUser(), tag, embedFieldList);
        context.getData().getModeration().sendModlogEvent(context.getGuild().getIdLong(), eventStore);
    }

    @Override
    public String command() {
        return "create";
    }

    @Override
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("tag.create", false);
    }

}
