/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

public class StringUtils {

    /**
     * Creates a progress bar using unicode bars and discord URL formatting
     * Format: [▬▬▬▬▬▬](url)▬▬▬▬ 60%
     *
     * @param percentage The percentage to be represented by the progress bar
     * @return The discord formatted progress bar
     */
    public static String getProgressBar(float percentage) {
        int bars = NumberUtils.round(percentage) / 10;
        return "[" + org.apache.commons.lang3.StringUtils.repeat("▬", bars) +
                "](https://github.com/CascadeBot)" +
                org.apache.commons.lang3.StringUtils.repeat("▬", 10 - bars) +
                " " + NumberUtils.round(percentage) + "%";

    }
}
