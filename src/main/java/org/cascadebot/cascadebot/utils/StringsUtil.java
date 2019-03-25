/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import net.dv8tion.jda.core.utils.Checks;
import org.apache.commons.lang3.StringUtils;

public class StringsUtil {

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
     * @param length     The length to make the progress bar
     * @return The discord formatted progress bar
     */
    public static String getProgressBar(float percentage, int length) {
        Checks.notNegative(length, "length");
        return getProgressBar(percentage, length, true);
    }

    /**
     * Creates a progress bar using unicode bars and discord URL formatting with a length of 10, and optional percentage after.
     * Format: [▬▬▬▬▬▬](url)▬▬▬▬ 60%
     *
     * @param percentage   The percentage to be represented by the progress bar
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
     * @param percentage   The percentage to be represented by the progress bar
     * @param length       The length to make the progress bar
     * @param percentAfter Weather or not to put the percent amount after
     * @return The discord formatted progress bar
     */
    public static String getProgressBar(float percentage, int length, boolean percentAfter) {
        Checks.notNegative(length, "length");
        int bars = (int) ((percentage / 100) * length);
        return "[" + StringUtils.repeat("▬", bars) + "](https://github.com/CascadeBot)" + // Bars representing the percentage, i.e. a length of 100 bars with 50% gives 50 bars wrapped in the URL
                StringUtils.repeat("▬", length - bars) + // The bars representing the unfilled percentage, i.e. for a percentage of 30%, this will be the number of bars for 70%
                " " + (percentAfter ? (Math.round(percentage) + "%") : ""); // If we want we can add a label showing the percentage overall, since this uses physical bars there cannot be a decimal percentage
    }

}
