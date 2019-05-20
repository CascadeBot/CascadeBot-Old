/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tag {

    // https://regex101.com/r/hlsgVW/1
    private static final Pattern TAG_PATTERN = Pattern.compile("\\{([A-z]+)(?::((?:,?\\w+)+))?}");

    private String content;
    private String category;

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
        Placeholder.class.getEnumConstants();
        Matcher matcher = TAG_PATTERN.matcher(content);
        while (matcher.find()) {
            Placeholder placeholder = EnumUtils.getEnum(Placeholder.class, matcher.group(1).toUpperCase());
            if (placeholder != null) {
                String[] args;
                if (matcher.group(2) != null) {
                    args = matcher.group(2).split(",");
                } else {
                    args = new String[0];
                }

                message = message.replace(matcher.group(), placeholder.getFunction().apply(commandContext, args));
            }
        }
        return message;
    }

}