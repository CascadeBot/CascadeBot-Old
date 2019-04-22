/*
  * Copyright (c) 2019 CascadeBot. All rights reserved.
  * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import org.apache.commons.lang.math.NumberUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tag {

    private static final Map<String, BiFunction<CommandContext, String[], String>> placeholders = new HashMap<>();
    // https://regex101.com/r/hlsgVW/1
    private static final Pattern TAG_PATTERN = Pattern.compile("\\{([A-z]+)(?::((?:,?\\w+)+))?}");

    private String content;
    private String category;

    static {
        // Guild
        addPlaceholder("server_region", (context -> context.getGuild().getRegion().getName()));
        addPlaceholder("server_name", (context -> context.getGuild().getName()));
        addPlaceholder("server_owner", (context -> context.getGuild().getOwner().getEffectiveName()));
        addPlaceholder("member_count", (context -> String.valueOf(context.getGuild().getMembers().size())));

        // Sender
        addPlaceholder("sender", (context -> context.getMember().getEffectiveName()));
        addPlaceholder("sender_id", (context -> context.getUser().getId()));
        addPlaceholder("sender_mention", (context -> context.getMember().getAsMention()));
        
        // Channel
        addPlaceholder("channel_name", (context -> context.getChannel().getName()));

        // Other
        addPlaceholder("time", (context -> FormatUtils.formatDateTime(OffsetDateTime.now())));

        addPlaceholder("args", (context, placeholderArgs) -> {
            if (placeholderArgs.length == 0) return "";
            int arg = NumberUtils.toInt(placeholderArgs[0], -1);
            if (arg == -1 || arg > context.getArgs().length - 1) return "";
            else return context.getArg(arg);
        });
    }
    
    private static void addPlaceholder(String key, Function<CommandContext, String> function) {
        placeholders.put(key, (context, strings) -> function.apply(context));
    }

    private static void addPlaceholder(String key, BiFunction<CommandContext, String[], String> function) {
        placeholders.put(key, function);
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

    public String formatTag(CommandContext commandContext) {
        String message = content;
        for (String key : placeholders.keySet()) {
            Matcher matcher = TAG_PATTERN.matcher(content);
            if (matcher.find()) {
                if (key.equalsIgnoreCase(matcher.group(1))) {
                    message = message.replace(matcher.group(), placeholders.get(key).apply(commandContext, matcher.group(2).split(",")));
                }
            }
        }
        return message;
    }

}