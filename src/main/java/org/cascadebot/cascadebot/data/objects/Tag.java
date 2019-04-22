/*
  * Copyright (c) 2019 CascadeBot. All rights reserved.
  * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Tag {

    private static final Map<String, Function<CommandContext, String>> placeholders = new HashMap<>();
    private String content;
    private String category;

    static {
        // Guild
        placeholders.put("{server_region}", (context -> context.getGuild().getRegion().getName()));
        placeholders.put("{server_name}", (context -> context.getGuild().getName()));
        placeholders.put("{server_owner}", (context -> context.getGuild().getOwner().getEffectiveName()));
        placeholders.put("{member_count}", (context -> String.valueOf(context.getGuild().getMembers().size())));

        // Sender
        placeholders.put("{sender}", (context -> context.getMember().getEffectiveName()));
        placeholders.put("{sender_id}", (context -> context.getUser().getId()));
        placeholders.put("{sender_mention}", (context -> context.getMember().getAsMention()));

        // Channel
        placeholders.put("{channel_name}", (context -> context.getChannel().getName()));

        // Other
        placeholders.put("{time}", (context -> FormatUtils.formatDateTime(OffsetDateTime.now())));
    }

    public Tag(String content, String category) {
        this.content = content;
        this.category = category;
    }

    private Tag() {} // For MongoDB :D

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}