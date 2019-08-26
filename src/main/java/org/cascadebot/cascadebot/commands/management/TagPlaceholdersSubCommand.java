/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.Placeholder;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagPlaceholdersSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String header = context.i18n("commands.tag.placeholders.header");
        Map<String, List<Placeholder>> placeholderGroupsMap = new HashMap<>();
        StringBuilder placeholderBuilder = new StringBuilder();
        for (Placeholder placeholder : Placeholder.values()) {
            String[] split = placeholder.name().split("_");
            if (placeholderGroupsMap.containsKey(split[0])) {
                List<Placeholder> items = placeholderGroupsMap.get(split[0]);
                items.add(placeholder);
                placeholderGroupsMap.put(split[0], items);
            } else {
                List<Placeholder> items = new ArrayList<>();
                items.add(placeholder);
                placeholderGroupsMap.put(split[0], items);
            }
        }

        for (Map.Entry<String, List<Placeholder>> entry : placeholderGroupsMap.entrySet()) {
            placeholderBuilder.append("**").append(entry.getKey()).append("**\n");
            for (Placeholder placeholder : entry.getValue()) {
                placeholderBuilder.append('`').append(placeholder.name().toLowerCase()).append("` - ").append(placeholder.getDescription(context.getLocale())).append('\n');
            }
            placeholderBuilder.append('\n');
        }

        List<Page> pageList = PageUtils.splitStringToStringPages(placeholderBuilder.toString(), 1000, '\n');
        for (Page page : pageList) {
            pageList.remove(page);
            PageObjects.StringPage stringPage = (PageObjects.StringPage) page;
            EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
            builder.setDescription(header + "\n\n" + stringPage.getContent());
            pageList.add(new PageObjects.EmbedPage(builder));
        }

        context.getUIMessaging().sendPagedMessage(pageList);
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
    public CascadePermission getPermission() {
        return CascadePermission.of("tag.placeholders", false);
    }

}
