/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.permissions;

import com.cascadebot.cascadebot.data.Config;

import java.util.Set;

/**
 * Security levels defined by Role IDs or User IDs, A level can be defined by an unlimited amount of roles
 * and users. Security levels are declared in ascending order.
 */
public enum SecurityLevel {
    STAFF,
    DEVELOPER,
    OWNER;

    public Set<Long> getIds() {
        return Config.INS.getSecurityLevels().get(this);
    }

    public boolean isAuthorised(SecurityLevel level) {
        return level.ordinal() <= this.ordinal();
    }

    /**
     * Returns the highest security level a user has access to.
     *
     * @param userId The ID of the user to check.
     * @param roleIds The list of role IDs to check against, this will almost always be the roles from the official server.
     * @return The highest security level the user has access to or {@code null} if they do not have access to anything.
     */
    public static SecurityLevel getLevelById(long userId, Set<Long> roleIds) {
        for (int i = SecurityLevel.values().length - 1; i >= 0; i--) {
            SecurityLevel level = SecurityLevel.values()[i];
            if (level.getIds().contains(userId)) return level;
            if (roleIds.stream().anyMatch(id -> level.getIds().contains(id))) return level;
        }
        return null;
    }

}
