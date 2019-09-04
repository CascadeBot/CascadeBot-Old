/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class CommandListenerTest {

    private static CommandListener listener = new CommandListener();

    @Test
    void testArgsNormal() {
        assertArrayEquals(new String[]{"onearg"}, listener.splitArgs("onearg"));
        assertArrayEquals(new String[]{"two", "args"}, listener.splitArgs("two args"));
        assertArrayEquals(new String[]{"three", "single", "args"}, listener.splitArgs("three single args"));
    }

    @Test
    void testArgsQuoted() {
        assertArrayEquals(new String[]{"onearg"}, listener.splitArgs("\"onearg\""));
        assertArrayEquals(new String[]{"two args"}, listener.splitArgs("\"two args\""));
        assertArrayEquals(new String[]{"three single args"}, listener.splitArgs("\"three single args\""));
        assertArrayEquals(new String[]{"onearg", "with", "extra"}, listener.splitArgs("\"onearg\" with extra"));
        assertArrayEquals(new String[]{"two args", "with", "extra"}, listener.splitArgs("\"two args\" with extra"));
    }

    @Test
    void testIgnoreQuotes() {
        assertArrayEquals(new String[]{"hel\"lo", "\""}, listener.splitArgs("hel\"lo \""));
    }

    @Test
    void testUnusual() {
        assertArrayEquals(new String[]{"!eval", "CascadeBot.INS.getUser(\"Test\")"}, listener.splitArgs("!eval CascadeBot.INS.getUser(\"Test\")"));
        assertArrayEquals(new String[]{"!eval", "CascadeBot.INS.getUser(\"Test\", \"123\")"}, listener.splitArgs("!eval CascadeBot.INS.getUser(\"Test\", \"123\")"));
    }

}