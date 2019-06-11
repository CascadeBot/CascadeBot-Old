/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.data.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascade.commandmeta.CommandContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Tag {

    // https://regex101.com/r/hlsgVW/1
    private static final Pattern TAG_PATTERN = Pattern.compile("\\{([A-z]+)(?::((?:,?\\w+)+))?}");

    private String content;
    private String category;

    public String formatTag(CommandContext commandContext) {
        String message = content;
        Placeholder.class.getEnumConstants();
        Matcher matcher = TAG_PATTERN.matcher(content);
        while (matcher.find()) {
            Placeholder placeholder = EnumUtils.getEnum(Placeholder.class, matcher.group(1).toUpperCase());
            if (placeholder != null) {
                String[] args;
                if(matcher.group(2) != null) {
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