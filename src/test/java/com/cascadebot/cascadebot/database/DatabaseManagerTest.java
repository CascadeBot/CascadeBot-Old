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
        assertEquals(DatabaseManager.buildStandardConnectionString("", "", Arrays.asList("localhost"), "", ""), "mongodb://localhost/"); // Single host test
        assertEquals(DatabaseManager.buildStandardConnectionString("", "", Arrays.asList("localhost", "localhost2"), "", ""), "mongodb://localhost,localhost2/"); // Multiple host test
        assertEquals(DatabaseManager.buildStandardConnectionString("admin", "", Arrays.asList("localhost"), "", ""), "mongodb://admin@localhost/"); // Username test
        assertEquals(DatabaseManager.buildStandardConnectionString("admin", "hello", Arrays.asList("localhost"), "", ""), "mongodb://admin:hello@localhost/"); // Username and password test
        assertEquals(DatabaseManager.buildStandardConnectionString("", "", Arrays.asList("localhost"), "test", ""), "mongodb://localhost/test"); // Database test
        assertEquals(DatabaseManager.buildStandardConnectionString("", "", Arrays.asList("localhost"), "", "test=2"), "mongodb://localhost/?test=2"); // Options test
        assertThrows(IllegalArgumentException.class, () -> DatabaseManager.buildStandardConnectionString("", "", new ArrayList<>(), "", "")); // Not allowed to pass empty list of hosts
    }


}