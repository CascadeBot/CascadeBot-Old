/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

public class NumberUtils {

    /**
     * Rounds number to a specified number of decimal places
     *
     * @param number The number to round
     * @param dp The number of decimal places to round to
     * @return The rounded number
     */
    public static double round(double number, int dp) {
        return Math.round(number * Math.pow(10, dp)) / Math.pow(10, dp);
    }

    public static int round(double number) {
        return (int) round(number, 0);
    }

}
