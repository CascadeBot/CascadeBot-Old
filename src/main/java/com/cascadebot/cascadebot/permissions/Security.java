/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.permissions;

import com.cascadebot.cascadebot.data.Config;
import com.cascadebot.shared.SecurityLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Security levels defined by Role IDs or User IDs, A level can be defined by an unlimited amount of roles
 * and users. Security levels are declared in ascending order.
 */
public class Security {

    public static Set<Long> getIds(SecurityLevel level) {
        return Config.INS.getSecurityLevels().get(level);
    }

    public static boolean isAuthorised(SecurityLevel level, SecurityLevel comparingLevel) {
        return comparingLevel.isAuthorised(level);
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
            if (Security.getIds(level).contains(userId)) return level;
            if (roleIds.stream().anyMatch(id -> Security.getIds(level).contains(id))) return level;
        }
        return null;
    }

}
