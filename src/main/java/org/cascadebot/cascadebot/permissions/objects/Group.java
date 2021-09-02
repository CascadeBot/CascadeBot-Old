/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.permissions.objects;

import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.cascadebot.cascadebot.data.database.BsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Group extends PermissionHolder implements BsonObject {

    // Base 55 with 5 chars gives 503284375 combinations, we should be ok for uniqueness
    // This is normal alphanumeric with similar characters removed for less errors when inputting
    private String id = RandomStringUtils.random(5, "abcdefghijkmnopqrstuvwxyzACDEFHJKLMNPRSTUVWXYZ123467890");

    private String name;

    private Set<Long> roleIds = Sets.newConcurrentHashSet();

    public Group(String name) {
        this.name = name;
    }

    public boolean linkRole(long roleId) {
        assertWriteMode();
        return roleIds.add(roleId);
    }

    public boolean unlinkRole(long roleId) {
        assertWriteMode();
        return roleIds.remove(roleId);
    }

    public Set<Long> getRoleIds() {
        return Set.copyOf(roleIds);
    }

    @Override
    HolderType getType() {
        return HolderType.GROUP;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        assertWriteMode();
        this.name = name;
    }

    @Override
    public void fromBson(@NotNull BsonDocument bsonDocument) {
        if (bsonDocument.containsKey("id")) {
            id = bsonDocument.get("id").asString().getValue();
        }
        if (bsonDocument.containsKey("name")) {
            name = bsonDocument.get("name").asString().getValue();
        }
        if (bsonDocument.containsKey("roleIds")) {
            roleIds.clear();
            for (BsonValue idBson : bsonDocument.get("roleIds").asArray()) {
                roleIds.add(idBson.asNumber().longValue());
            }
        }
        super.fromBson(bsonDocument);
    }
}
