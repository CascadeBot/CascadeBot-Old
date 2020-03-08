/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Tag implements PermissionObject {

    // https://regex101.com/r/hlsgVW/1
    private static final Pattern TAG_PATTERN = Pattern.compile("\\{([A-z]+)(?::((?:,?\\w+)+))?}");

    private String name;
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

    @Override
    public String getPermission() {
        return category + "." + name;
    }

    @Override
    public String getParent() {
        return "tag";
    }

    @Override
    public Module cascadeModule() {
        return Module.MANAGEMENT;
    }
}