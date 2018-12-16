/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.permissions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityLevelTest {

    @Test
    void isAuthorisedTest() {
        assertTrue(SecurityLevel.OWNER.isAuthorised(SecurityLevel.STAFF));
        assertTrue(SecurityLevel.OWNER.isAuthorised(SecurityLevel.DEVELOPER));
        assertTrue(SecurityLevel.DEVELOPER.isAuthorised(SecurityLevel.STAFF));
        assertTrue(SecurityLevel.STAFF.isAuthorised(SecurityLevel.STAFF));

        assertFalse(SecurityLevel.STAFF.isAuthorised(SecurityLevel.OWNER));
        assertFalse(SecurityLevel.STAFF.isAuthorised(SecurityLevel.DEVELOPER));
        assertFalse(SecurityLevel.DEVELOPER.isAuthorised(SecurityLevel.OWNER));
    }

}