/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import java.util.Set;

/**
 * Security levels defined by Role IDs or User IDs, A level can be defined by an unlimited amount of roles
 * and users.
 */
public enum SecurityLevel {
    STAFF,
    DEVELOPER,
    OWNER;

    public Set<Long> getId() {
        return Config.INS.getSecurityLevels().get(this);
    }

}
