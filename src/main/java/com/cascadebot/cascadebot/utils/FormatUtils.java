/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import org.apache.commons.lang3.StringUtils;

public class FormatUtils {

    public static String makeAsciiTable(java.util.List<String> headers, java.util.List<java.util.List<String>> table, String footer) {
        StringBuilder sb = new StringBuilder();
        int padding = 1;
        int[] widths = new int[headers.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = 0;
        }
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).length() > widths[i]) {
                widths[i] = headers.get(i).length();
            }
        }
        for (java.util.List<String> row : table) {
            for (int i = 0; i < row.size(); i++) {
                String cell = row.get(i);
                if (cell.length() > widths[i]) {
                    widths[i] = cell.length();
                }
            }
        }
        sb.append("```").append("md").append("\n");
        StringBuilder formatLine = new StringBuilder("|");
        for (int width : widths) {
            formatLine.append(" %-").append(width).append("s |");
        }
        formatLine.append("\n");
        sb.append(appendSeparatorLine(padding, widths));
        sb.append(String.format(formatLine.toString(), headers.toArray()));
        sb.append(appendSeparatorLine(padding, widths));
        for (java.util.List<String> row : table) {
            sb.append(String.format(formatLine.toString(), row.toArray()));
        }
        if (footer != null) {
            sb.append(appendSeparatorLine(padding, widths));
            sb.append(getFooter(footer, padding, widths));
        }
        sb.append(appendSeparatorLine(padding, widths));
        sb.append("```");
        return sb.toString();
    }

    private static String appendSeparatorLine(int padding, int... sizes) {
        boolean first = true;
        StringBuilder ret = new StringBuilder();
        for (int size : sizes) {
            if (first) {
                first = false;
                ret.append("+").append(StringUtils.repeat("-", size + padding * 2));
            } else {
                ret.append("+").append(StringUtils.repeat("-", size + padding * 2));
            }
        }
        return ret.append("+").append("\n").toString();
    }

    private static String getFooter(String footer, int padding, int... sizes) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        int total = 0;
        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            total += size + (i == sizes.length - 1 ? 0 : 1) + padding * 2;
        }
        sb.append(footer);
        sb.append(StringUtils.repeat(" ", total - footer.length()));
        sb.append("|\n");
        return sb.toString();
    }
}
