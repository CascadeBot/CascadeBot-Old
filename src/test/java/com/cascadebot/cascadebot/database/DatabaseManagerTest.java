/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.database;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class DatabaseManagerTest {

    @Test
    public void testBuildStandardConnectionString() {
        assertEquals(DatabaseManager.buildStandardConnectionString("", "", new String[]{"localhost"}, "", ""), "mongodb://localhost/"); // Single host test
        assertEquals(DatabaseManager.buildStandardConnectionString("", "", new String[]{"localhost", "localhost2"}, "", ""), "mongodb://localhost,localhost2/"); // Multiple host test
        assertEquals(DatabaseManager.buildStandardConnectionString("admin", "", new String[]{"localhost"}, "", ""), "mongodb://admin@localhost/"); // Username test
        assertEquals(DatabaseManager.buildStandardConnectionString("admin", "hello", new String[]{"localhost"}, "", ""), "mongodb://admin:hello@localhost/"); // Username and password test
        assertEquals(DatabaseManager.buildStandardConnectionString("", "", new String[]{"localhost"}, "test", ""), "mongodb://localhost/test"); // Database test
        assertEquals(DatabaseManager.buildStandardConnectionString("", "", new String[]{"localhost"}, "", "test=2"), "mongodb://localhost/?test=2"); // Options test
        assertThrows(IllegalArgumentException.class, () -> DatabaseManager.buildStandardConnectionString("", "", new String[]{}, "", "")); // Not allowed to pass empty list of hosts
    }

    @Test
    public void testBuildSRVConnectionString() {
        assertEquals(DatabaseManager.buildSRVConnectionString("", "", "localhost", "", ""), "mongodb+srv://localhost/"); // Single host test
        assertEquals(DatabaseManager.buildSRVConnectionString("admin", "", "localhost", "", ""), "mongodb+srv://admin@localhost/"); // Username test
        assertEquals(DatabaseManager.buildSRVConnectionString("admin", "hello", "localhost", "", ""), "mongodb+srv://admin:hello@localhost/"); // Username and password test
        assertEquals(DatabaseManager.buildSRVConnectionString("", "", "localhost", "test", ""), "mongodb+srv://localhost/test"); // Database test
        assertEquals(DatabaseManager.buildSRVConnectionString("", "", "localhost", "", "test=2"), "mongodb+srv://localhost/?test=2"); // Options test
        assertThrows(IllegalArgumentException.class, () -> DatabaseManager.buildSRVConnectionString("", "", "", "", "")); // Not allowed to pass blank host
    }


}