/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Test
    void toStringTest() {
        assertEquals(Version.of(1, 0, 0).toString(), "1.0.0");
        assertEquals(Version.of(-1, -10, -20).toString(), "1.10.20");
    }

    @Test
    void equalsTest() {
        assertEquals(Version.of(1, 0, 0), Version.of(1, 0, 0));
        assertNotEquals(Version.of(1, 0, 0), Version.of(1, 0, 1));
    }

    @Test
    void hashCodeTest() {
        assertEquals(Version.of(1, 0, 0).hashCode(), Version.of(1, 0, 0).hashCode());
        assertNotEquals(Version.of(1, 0, 0).hashCode(), Version.of(1, 0, 1).hashCode());
    }

    @Test
    void compareToTest() {
        assertEquals(-1, Version.of(1, 0, 0).compareTo(Version.of(1, 0, 1)));
        assertEquals(0, Version.of(1, 0, 0).compareTo(Version.of(1, 0, 0)));
    }

}