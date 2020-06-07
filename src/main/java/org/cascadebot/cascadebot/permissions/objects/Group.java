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

import java.util.Set;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Group extends PermissionHolder {

    // Base 55 with 5 chars gives 503284375 combinations, we should be ok for uniqueness
    // This is normal alphanumeric with similar characters removed for less errors when inputting
    private String id = RandomStringUtils.random(5, "abcdefghijkmnopqrstuvwxyzACDEFHJKLMNPRSTUVWXYZ123467890");

    @Getter
    @Setter
    private String name;

    private Set<Long> roleIds = Sets.newConcurrentHashSet();

    public Group(String name) {
        this.name = name;
    }

    public boolean linkRole(long roleId) {
        return roleIds.add(roleId);
    }

    public boolean unlinkRole(long roleId) {
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
}
