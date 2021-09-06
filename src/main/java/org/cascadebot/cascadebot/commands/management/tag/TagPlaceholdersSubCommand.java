/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.tag;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;
import org.cascadebot.cascadebot.utils.placeholders.Placeholder;
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects;

import java.util.List;

public class TagPlaceholdersSubCommand extends DeprecatedSubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String header = context.i18n("commands.tag.placeholders.header");
        StringBuilder placeholderBuilder = new StringBuilder();

        for (Placeholder<CommandContext> placeholder : PlaceholderObjects.getTags().getPlaceholders()) {
            placeholderBuilder
                    .append('`')
                    .append(placeholder.getLocalisedInfo().get(context.getLocale()).getKey())
                    .append("` - ")
                    .append(placeholder.getLocalisedInfo().get(context.getLocale()).getDescription())
                    .append("\n");
        }

        List<Page> pageList = PageUtils.splitStringToStringPages(placeholderBuilder.toString(), 1000, '\n');
        for (Page page : pageList) {
            pageList.remove(page);
            PageObjects.StringPage stringPage = (PageObjects.StringPage) page;
            EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
            builder.setDescription(header + "\n\n" + stringPage.getContent());
            pageList.add(new PageObjects.EmbedPage(builder));
        }

        context.getUiMessaging().sendPagedMessage(pageList);
    }

    @Override
    public String command() {
        return "placeholders";
    }

    @Override
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("tag.placeholders", false);
    }

}
