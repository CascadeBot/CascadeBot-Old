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
import org.bson.BsonDocument;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.database.BsonObject;
import org.cascadebot.cascadebot.data.database.DataHandler;
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Tag extends PermissionObject implements BsonObject {

    // https://regex101.com/r/hlsgVW/1
    private static final Pattern TAG_PATTERN = Pattern.compile("\\{([A-z]+)(?::((?:,?\\w+)+))?}");

    private String name;
    private String content;
    private String category;

    public Tag(String name, String content, String category) {
        this.name = name;
        this.content = content;
        this.category = category;
    }

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

    @Override
    public void fromBson(@NotNull BsonDocument bsonDocument) {
        if (bsonDocument.containsKey("name")) {
            name = bsonDocument.get("name").asString().getValue();
        }
        if (bsonDocument.containsKey("content")) {
            content = bsonDocument.get("content").asString().getValue();
        }
        if (bsonDocument.containsKey("category")) {
            category = bsonDocument.get("category").asString().getValue();
        }
    }

    @Override
    public void handleRemove(@NotNull DataHandler.RemovedTree tree) {

    }
}