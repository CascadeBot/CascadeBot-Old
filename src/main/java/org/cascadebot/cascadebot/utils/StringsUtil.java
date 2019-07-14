/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.internal.utils.Checks;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class StringsUtil {

    /**
     * Creates a progress bar using unicode bars and discord URL formatting with a length of 10.
     * Format: [▬▬▬▬▬▬](url)▬▬▬▬ 60%
     *
     * @param percentage The percentage to be represented by the progress bar
     * @return The discord formatted progress bar
     */
    public static String getProgressBarEmbed(float percentage) {
        return getProgressBarEmbed(percentage, 10, true);
    }

    /**
     * Creates a progress bar using unicode bars and discord URL formatting with a specified length.
     * Format: [▬▬▬▬▬▬](url)▬▬▬▬ 60%
     *
     * @param percentage The percentage to be represented by the progress bar
     * @param length     The length to make the progress bar
     * @return The discord formatted progress bar
     */
    public static String getProgressBarEmbed(float percentage, int length) {
        Checks.notNegative(length, "length");
        return getProgressBarEmbed(percentage, length, true);
    }

    /**
     * Creates a progress bar using unicode bars and discord URL formatting with a length of 10, and optional percentage after.
     * Format: [▬▬▬▬▬▬](url)▬▬▬▬ 60%
     *
     * @param percentage   The percentage to be represented by the progress bar
     * @param percentAfter Weather or not to put the percent amount after.
     * @return The discord formatted progress bar
     */
    public static String getProgressBarEmbed(float percentage, boolean percentAfter) {
        return getProgressBarEmbed(percentage, 10, percentAfter);
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
    public static String getProgressBarEmbed(float percentage, int length, boolean percentAfter) {
        Checks.notNegative(length, "length");
        int bars = (int) ((percentage / 100) * length);
        return "[" + StringUtils.repeat("▬", bars) + "](https://github.com/CascadeBot)" + // Bars representing the percentage, i.e. a length of 100 bars with 50% gives 50 bars wrapped in the URL
                StringUtils.repeat("▬", length - bars) + // The bars representing the unfilled percentage, i.e. for a percentage of 30%, this will be the number of bars for 70%
                " " + (percentAfter ? (Math.round(percentage) + "%") : ""); // If we want we can add a label showing the percentage overall, since this uses physical bars there cannot be a decimal percentage
    }

    /**
     * Creates a progress bar using unicode bars with a length of 10.
     * Format: ══════⚪─── 60%
     *
     * @param percentage The percentage to be represented by the progress bar
     * @return The discord formatted progress bar
     */
    public static String getProgressBar(float percentage) {
        return getProgressBar(percentage, 10, true);
    }

    /**
     * Creates a progress bar using unicode bars with a specified length.
     * Format: ══════⚪─── 60%
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
     * Creates a progress bar using unicode bars with a length of 10, and optional percentage after.
     * Format: ══════⚪─── 60%
     *
     * @param percentage   The percentage to be represented by the progress bar
     * @param percentAfter Weather or not to put the percent amount after.
     * @return The discord formatted progress bar
     */
    public static String getProgressBar(float percentage, boolean percentAfter) {
        return getProgressBar(percentage, 10, percentAfter);
    }

    /**
     * Creates a progress bar using unicode bars with a specified length, and optional percentage after.
     * Format: ══════⚪─── 60%
     *
     * @param percentage   The percentage to be represented by the progress bar
     * @param length       The length to make the progress bar
     * @param percentAfter Weather or not to put the percent amount after
     * @return The discord formatted progress bar
     */
    public static String getProgressBar(float percentage, int length, boolean percentAfter) {
        Checks.notNegative(length, "length");
        int bars = (int) ((percentage / 100) * length);
        return StringUtils.repeat("═", bars) + "⚪" + // Bars representing the percentage, i.e. a length of 100 bars with 50% gives 50 bars wrapped in the URL
                StringUtils.repeat("─", length - bars - 1) + // The bars representing the unfilled percentage, i.e. for a percentage of 30%, this will be the number of bars for 70%
                " " + (percentAfter ? (Math.round(percentage) + "%") : ""); // If we want we can add a label showing the percentage overall, since this uses physical bars there cannot be a decimal percentage
    }

    /**
     * Truncates a String to the given length. With ellipses. This truncates from the end.
     * Note that this will truncate the string to three less then the length because of the ellipses.
     * (The total length of the string would be the provided length).
     *
     * @param string The string to truncate.
     * @param length The amount to truncate the string to.
     * @return The truncated String.
     */
    public static String truncate(String string, int length) {
        return truncate(string, length, true);
    }

    /**
     * Truncates a String to the given length. This truncates from the end.
     * If you do use eclipse the length would be three less then the provided length due to the ellipses.
     * (The total length of the string would be the provided length).
     *
     * @param string  The string to add them to.
     * @param length  The amount to truncate the string to.
     * @param ellipse Weather or not to use ellipses.
     * @return The truncated String.
     */
    public static String truncate(String string, int length, boolean ellipse) {
        return string.substring(0, Math.min(string.length(), length - (ellipse ? 3 : 0))) + (string.length() >
                length - (ellipse ? 3 : 0) && ellipse ? "..." : "");
    }

}
