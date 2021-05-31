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
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects;

import java.util.regex.Pattern;

import static org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Tag extends PermissionObject {

    private final String objClass = this.getClass().getName();

    // https://regex101.com/r/hlsgVW/1
    private static final Pattern TAG_PATTERN = Pattern.compile("\\{([A-z]+)(?::((?:,?\\w+)+))?}");

    private String name;
    private String content;
    private String category;

    public String formatTag(CommandContext commandContext) {
        return PlaceholderObjects.getTags().formatMessage(commandContext.getLocale(), content, commandContext);
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

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        assertWriteMode();
        this.category = category;
    }

    public void setContent(String content) {
        assertWriteMode();
        this.content = content;
    }

    public void setName(String name) {
        assertWriteMode();
        this.name = name;
    }
}