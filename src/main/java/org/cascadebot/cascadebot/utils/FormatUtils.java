/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class FormatUtils {

    private static final Pattern UNICODE_REGEX = Pattern.compile("\\|(g:)?([A-Za-z_]+)\\|");

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
        if (embed.getTitle() != null) {
            sb.append("__**").append(embed.getTitle()).append("**__\n");
        }
        if (embed.getDescription() != null) {
            sb.append(embed.getDescription()).append("\n\n");
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
                sb.append(field.getValue()).append("\n\n");
            }
        }
        if (inline != null) {
            sb.append(getFormattedInlineFields(inline)).append("\n\n");
        }
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

    /**
     * Formats date and time by RFC 1123
     *
     * @param dateTime the date time to be formatted
     * @return The formatted string
     * @deprecated Use the i18n instead
     */
    @Deprecated
    public static String formatDateTime(OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public static String formatDateTime(OffsetDateTime dateTime, Locale locale) {
        return SimpleDateFormat.getDateTimeInstance(DateFormat.RELATIVE_LONG, DateFormat.LONG, locale.getULocale()).format(new Date(dateTime.toEpochSecond() * 1000));
    }

    @Deprecated
    public static <T extends Enum> String formatEnum(T theEnum) {
        return StringUtils.capitalize(theEnum.name().toLowerCase().replace("_", " "));
    }

    public static <T extends Enum> String formatEnum(T theEnum, Locale locale) {
        String path = "enums." + theEnum.getClass().getSimpleName().toLowerCase() + "." + theEnum.name().toLowerCase();
        if (!Language.hasLanguageEntry(locale, path)) {
            return formatEnum(theEnum);
        }
        return Language.i18n(locale, path);
    }

    public static String formatUnicode(String stringToFormat) {
        Matcher matcher = UNICODE_REGEX.matcher(stringToFormat);
        String formatted = stringToFormat;
        while (matcher.find()) {
            // If matching a normal @<emoji>@ otherwise it's a
            // global emote @g:<emote>@
            if (matcher.group(1) == null) {
                Emoji emoji = EmojiManager.getForAlias(matcher.group(2).toLowerCase());
                if (emoji != null) {
                    formatted = formatted.replace(matcher.group(), emoji.getUnicode());
                }
            } else {
                Long emoteId = Config.INS.getGlobalEmotes().get(matcher.group(2));
                Emote emote = CascadeBot.INS.getShardManager().getEmoteById(Optional.ofNullable(emoteId).orElse(0L));
                if (emote != null) {
                    formatted = formatted.replace(matcher.group(), emote.getAsMention());
                }
            }
        }
        formatted = formatted.replace("@infinity@", "\u221e");
        formatted = formatted.replace("@zero_width_space@", "\u200B");
        return formatted;
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

    public static String formatPrefix(String prefix, String string) {
        return string.replace("|%|", prefix);
    }

    public static String formatLongTimeMills(long time) {
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long mins = TimeUnit.MILLISECONDS.toMinutes(time) - (hours * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - (TimeUnit.MILLISECONDS.toMinutes(time) * 60);
        return String.format("%02d:%02d:%02d", hours, mins, seconds);
    }

}
