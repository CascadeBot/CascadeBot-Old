/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.UnicodeConstants;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FormatUtils {

    //region Table methods
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
    //endregion

    //region Embed methods
    public static String formatEmbed(MessageEmbed embed) {
        StringBuilder sb = new StringBuilder();
        sb.append("__**").append(embed.getTitle()).append("**__\n");
        if (embed.getDescription() != null) {
            sb.append(Joiner.on("\n").join(Splitter.fixedLength(100).split(embed.getDescription()))).append("\n\n");
        }
        List<MessageEmbed.Field> inline = null;
        int i = 0;
        for (MessageEmbed.Field field : embed.getFields()) {
            if (field.isInline() && field.getName().length() <= 20 && field.getValue().length() <= 20) {
                if (inline == null) {
                    inline = new ArrayList<>();
                }
                inline.add(field);
                if (i == 2) {
                    sb.append(getFormattedInlineFields(inline)).append("\n\n");
                    inline.clear();
                    i = 0;
                }
                i++;
            } else {
                if (inline != null) {
                    sb.append(getFormattedInlineFields(inline)).append("\n\n");
                    inline.clear();
                }
                i = 0;
                sb.append("**").append(field.getName()).append("**\n");
                sb.append(Joiner.on("\n").join(Splitter.fixedLength(100).split(field.getValue()))).append("\n\n");
            }
        }
        sb.append(getFormattedInlineFields(inline)).append("\n\n");
        sb.append("_").append(embed.getFooter().getText()).append("_");

        return sb.toString();
    }

    private static String getFormattedInlineFields(List<MessageEmbed.Field> fieldList) {
        StringBuilder sb = new StringBuilder();

        List<String> header = new ArrayList<>();
        List<String> body = new ArrayList<>();

        for (MessageEmbed.Field field : fieldList) {
            header.add(field.getName());
            body.add(field.getValue());
        }

        sb.append('`');

        for (String head : header) {
            sb.append(String.format("%-25s", head));
        }

        sb.append(UnicodeConstants.ZERO_WIDTH_SPACE + "`"); // Zero width space

        sb.append("\n");

        sb.append('`');

        for (String bodyString : body) {
            sb.append(String.format("%-25s", bodyString));
        }

        sb.append(UnicodeConstants.ZERO_WIDTH_SPACE + "`"); // Zero width space

        return sb.toString();
    }
    //endregion

    public static String formatDateTime(OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public static <T extends Enum> String formatEnum(T theEnum) {
        return StringUtils.capitalize(theEnum.name().toLowerCase().replace("_", " "));
    }

    /**
     * Rounds number to a specified number of decimal places
     *
     * @param number The number to round
     * @param dp     The number of decimal places to round to
     * @return The rounded number
     */
    public static double round(double number, int dp) {
        return Math.round(number * Math.pow(10, dp)) / Math.pow(10, dp);
    }

}
