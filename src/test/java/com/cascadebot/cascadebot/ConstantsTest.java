package com.cascadebot.cascadebot;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConstantsTest {

    @Test
    public void testIntegerRegex() {
        assertTrue(Constants.INTEGER_REGEX.matcher("23").matches());
        assertTrue(Constants.INTEGER_REGEX.matcher("2462337846346348467").matches());
        assertTrue(Constants.INTEGER_REGEX.matcher("-1").matches());
        assertFalse(Constants.INTEGER_REGEX.matcher("-1.0").matches());
        assertFalse(Constants.INTEGER_REGEX.matcher("2.3").matches());
        assertFalse(Constants.INTEGER_REGEX.matcher("Hello world").matches());
    }

    @Test
    public void testDecimalRegex() {
        assertFalse(Constants.DECIMAL_REGEX.matcher("23").matches());
        assertFalse(Constants.DECIMAL_REGEX.matcher("2462337846346348467").matches());
        assertTrue(Constants.DECIMAL_REGEX.matcher("2.3").matches());
        assertTrue(Constants.DECIMAL_REGEX.matcher("-2.587").matches());
        assertFalse(Constants.DECIMAL_REGEX.matcher("Hello world").matches());
    }



}
