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
public class CascadeSecurityLevel {

    private static Map<SecurityLevel, CascadeSecurityLevel> securityLevelMap = new HashMap<>();

    SecurityLevel level;
    private CascadeSecurityLevel(SecurityLevel level) {
        this.level = level;
    }

    public static CascadeSecurityLevel getSecurityLevel(SecurityLevel level) {
        return securityLevelMap.getOrDefault(level, new CascadeSecurityLevel(level));
    }

    public Set<Long> getIds() {
        return Config.INS.getSecurityLevels().get(this);
    }

    public boolean isAuthorised(CascadeSecurityLevel level) {
        return level.level.ordinal() <= this.level.ordinal();
    }

    public boolean isAuthorised(SecurityLevel level) {
        return level.ordinal() <= this.level.ordinal();
    }

    public static SecurityLevel getSecurityLevelById(long userId, Set<Long> roleIds){
        if(getLevelById(userId, roleIds) == null) {
            return null;
        }
        return getLevelById(userId, roleIds).level;
    }

    /**
     * Returns the highest security level a user has access to.
     *
     * @param userId The ID of the user to check.
     * @param roleIds The list of role IDs to check against, this will almost always be the roles from the official server.
     * @return The highest security level the user has access to or {@code null} if they do not have access to anything.
     */
    public static CascadeSecurityLevel getLevelById(long userId, Set<Long> roleIds) {
        for (int i = SecurityLevel.values().length - 1; i >= 0; i--) {
            CascadeSecurityLevel level = CascadeSecurityLevel.getSecurityLevel(SecurityLevel.values()[i]);
            if (level.getIds().contains(userId)) return level;
            if (roleIds.stream().anyMatch(id -> level.getIds().contains(id))) return level;
        }
        return null;
    }

    public SecurityLevel getLevel() {
        return level;
    }

}
