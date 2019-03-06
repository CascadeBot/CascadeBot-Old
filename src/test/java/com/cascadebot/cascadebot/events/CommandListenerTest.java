/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.events;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CommandListenerTest {

    private static CommandListener listener = new CommandListener();

    @Test
    void splitArgs() {
        String input = "Hello \"world test\" this is a test\"";
        assertArrayEquals(listener.splitArgs(input), new String[]{"Hello", "world test", "this", "is", "a", "test"});
        input = "\" \" \" \"";
        assertArrayEquals(listener.splitArgs(input), new String[]{" ", " "});
        input = "Hello \"World\"";
        assertArrayEquals(listener.splitArgs(input), new String[]{"Hello", "World"});

    }

}