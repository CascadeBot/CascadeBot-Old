/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade;

import org.cascadebot.shared.Regex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstantsTest {

    @Test
    public void testIntegerRegex() {
        assertTrue(Regex.INTEGER_REGEX.matcher("23").matches());
        assertTrue(Regex.INTEGER_REGEX.matcher("2462337846346348467").matches());
        assertTrue(Regex.INTEGER_REGEX.matcher("-1").matches());
        assertFalse(Regex.INTEGER_REGEX.matcher("-1.0").matches());
        assertFalse(Regex.INTEGER_REGEX.matcher("2.3").matches());
        assertFalse(Regex.INTEGER_REGEX.matcher("Hello world").matches());
    }

    @Test
    public void testPositiveIntegerRegex() {
        assertTrue(Regex.POSITIVE_INTEGER_REGEX.matcher("23").matches());
        assertTrue(Regex.POSITIVE_INTEGER_REGEX.matcher("2462337846346348467").matches());
        assertFalse(Regex.POSITIVE_INTEGER_REGEX.matcher("-1").matches());
        assertFalse(Regex.POSITIVE_INTEGER_REGEX.matcher("-1.0").matches());
        assertFalse(Regex.POSITIVE_INTEGER_REGEX.matcher("2.3").matches());
        assertFalse(Regex.POSITIVE_INTEGER_REGEX.matcher("Hello world").matches());
    }

    @Test
    public void testDecimalRegex() {
        assertFalse(Regex.DECIMAL_REGEX.matcher("23").matches());
        assertFalse(Regex.DECIMAL_REGEX.matcher("2462337846346348467").matches());
        assertTrue(Regex.DECIMAL_REGEX.matcher("2.3").matches());
        assertTrue(Regex.DECIMAL_REGEX.matcher("-2.587").matches());
        assertFalse(Regex.DECIMAL_REGEX.matcher("Hello world").matches());
    }

    @Test
    public void testPositiveDecimalRegex() {
        assertFalse(Regex.POSITIVE_DECIMAL_REGEX.matcher("23").matches());
        assertFalse(Regex.POSITIVE_DECIMAL_REGEX.matcher("2462337846346348467").matches());
        assertTrue(Regex.POSITIVE_DECIMAL_REGEX.matcher("2.3").matches());
        assertFalse(Regex.POSITIVE_DECIMAL_REGEX.matcher("-2.587").matches());
        assertFalse(Regex.POSITIVE_DECIMAL_REGEX.matcher("Hello world").matches());
    }


}
