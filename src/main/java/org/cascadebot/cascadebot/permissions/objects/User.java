/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.permissions.objects;

import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.cascadebot.cascadebot.data.database.BsonObject;
import org.cascadebot.cascadebot.data.database.DataHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends PermissionHolder implements BsonObject {

    private final Set<String> groups = Sets.newConcurrentHashSet();

    public boolean addGroup(Group group) {
        assertWriteMode();
        return groups.add(group.getId());
    }

    public boolean removeGroup(Group group) {
        assertWriteMode();
        return groups.remove(group.getId());
    }

    public Set<String> getGroupIds() {
        return Set.copyOf(groups);
    }

    @Override
    HolderType getType() {
        return HolderType.USER;
    }

    @Override
    public void fromBson(@NotNull BsonDocument bsonDocument) {
        if (bsonDocument.containsKey("groups")) {
            groups.clear();
            for (BsonValue groupBson : bsonDocument.get("groups").asArray()) {
                groups.add(groupBson.asString().getValue());
            }
        }
        super.fromBson(bsonDocument);
    }

    @Override
    public void handleRemove(@NotNull DataHandler.RemovedTree tree) {

    }
}
