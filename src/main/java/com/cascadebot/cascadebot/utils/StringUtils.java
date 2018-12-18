/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

public class StringUtils {

    /**
     * Creates a progress bar using unicode bars and discord URL formatting with a length of 10.
     * Format: [▬▬▬▬▬▬](url)▬▬▬▬ 60%
     *
     * @param percentage The percentage to be represented by the progress bar
     * @return The discord formatted progress bar
     */
    public static String getProgressBar(float percentage) {
        return getProgressBar(percentage, 10, true);
    }

    /**
     * Creates a progress bar using unicode bars and discord URL formatting with a specified length.
     * Format: [▬▬▬▬▬▬](url)▬▬▬▬ 60%
     *
     * @param percentage The percentage to be represented by the progress bar
     * @param length The length to make the progress bar
     * @return The discord formatted progress bar
     */
    public static String getProgressBar(float percentage, int length) {
        return getProgressBar(percentage, length, true);
    }

    /**
     * Creates a progress bar using unicode bars and discord URL formatting with a length of 10, and optional percentage after.
     * Format: [▬▬▬▬▬▬](url)▬▬▬▬ 60%
     *
     * @param percentage The percentage to be represented by the progress bar
     * @param percentAfter Weather or not to put the percent amount after.
     * @return The discord formatted progress bar
     */
    public static String getProgressBar(float percentage, boolean percentAfter) {
        return getProgressBar(percentage, 10, percentAfter);
    }

    /**
     * Creates a progress bar using unicode bars and discord URL formatting with a specified length, and optional percentage after.
     * Format: [▬▬▬▬▬▬](url)▬▬▬▬ 60%
     *
     * @param percentage
     * @param length
     * @param percentAfter
     * @return
     */
    public static String getProgressBar(float percentage, int length, boolean percentAfter) {
        int bars = Math.round((percentage / 100) * length);
        return "[" + org.apache.commons.lang3.StringUtils.repeat("▬", bars) +
                "](https://github.com/CascadeBot)" +
                org.apache.commons.lang3.StringUtils.repeat("▬", length - bars) +
                " " + (percentAfter ? ( Math.round(percentage) + "%") : "");
    }
}
