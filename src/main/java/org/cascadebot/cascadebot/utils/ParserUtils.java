package org.cascadebot.cascadebot.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.binaryoverload.JSONConfig;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {

    private static final Pattern TEXT_TIME_PARSER = Pattern.compile("(?<weeks>\\d+) ?w|(?<days>\\d+) ?d|(?<hours>\\d+) ?h|(?<minutes>\\d+) ?m|(?<seconds>\\d+) ?s");
    private static final Pattern DIGITAL_TIME_REGEX = Pattern.compile("(\\d+):(\\d+)(?::(\\d+))?");

    /**
     * Attempts to parse a time from a input string.
     *
     * Can parse three types of input:
     * <ul>
     *     <li>Number of seconds</li>
     *     <li>Parses a time in mm:ss or hh:mm:ss format</li>
     *     <li>Parses a time using 2h6m20s format <b>(1)</b></li>
     * </ul>
     * (1) accepts a variety of units including w (weeks), d (days), h (hours), m (minutes), s (seconds).
     * Works with or without a space before the unit.
     *
     * @param input
     * @return The number of milliseconds
     */
    public static long parseTime(String input) {
        try {
            return Long.parseLong(input) * 1000;
        } catch (NumberFormatException e) {
            if (DIGITAL_TIME_REGEX.matcher(input).matches()) {
                return parseDigitalTime(input);
            } else {
                return parseTextTime(input, true);
            }
        }
    }

    public static long parseTextTime(String input, boolean onlyAllowOnce) {
        Matcher matcher = TEXT_TIME_PARSER.matcher(input);

        // These booleans represent whether the unit of time has been found yet
        boolean weeksParsed = false;
        boolean daysParsed = false;
        boolean hoursParsed = false;
        boolean minutesParsed = false;
        boolean secondsParsed = false;

        String allowOnceMessage = "Only one of each time unit from the list: w, d, h, m, s is allowed!";

        // The result in milliseconds
        long millis = 0;
        while (matcher.find()) {
            if (matcher.group("weeks") != null) {
                if (weeksParsed && onlyAllowOnce) {
                    throw new IllegalArgumentException(allowOnceMessage);
                }
                weeksParsed = true;
                millis += TimeUnit.DAYS.toMillis(Long.parseLong(matcher.group("weeks")) * 7);
            } else if (matcher.group("days") != null) {
                if (daysParsed && onlyAllowOnce) {
                    throw new IllegalArgumentException(allowOnceMessage);
                }
                daysParsed = true;
                millis += TimeUnit.DAYS.toMillis(Long.parseLong(matcher.group("days")));
            } else if (matcher.group("hours") != null) {
                if (hoursParsed && onlyAllowOnce) {
                    throw new IllegalArgumentException(allowOnceMessage);
                }
                hoursParsed = true;
                millis += TimeUnit.HOURS.toMillis(Long.parseLong(matcher.group("hours")));
            } else if (matcher.group("minutes") != null) {
                if (minutesParsed && onlyAllowOnce) {
                    throw new IllegalArgumentException(allowOnceMessage);
                }
                minutesParsed = true;
                millis += TimeUnit.MINUTES.toMillis(Long.parseLong(matcher.group("minutes")));
            } else if (matcher.group("seconds") != null) {
                if (secondsParsed && onlyAllowOnce) {
                    throw new IllegalArgumentException(allowOnceMessage);
                }
                secondsParsed = true;
                millis += TimeUnit.SECONDS.toMillis(Long.parseLong(matcher.group("seconds")));
            }
        }
        return millis;
    }

    public static long parseDigitalTime(String input) {
        Matcher matcher = DIGITAL_TIME_REGEX.matcher(input);
        if (matcher.matches()) {
            if (matcher.group(3) == null) {
                return TimeUnit.MINUTES.toMillis(Long.parseLong(matcher.group(1))) +
                        TimeUnit.SECONDS.toMillis(Long.parseLong(matcher.group(2)));
            } else {
                return TimeUnit.HOURS.toMillis(Long.parseLong(matcher.group(1))) +
                        TimeUnit.MINUTES.toMillis(Long.parseLong(matcher.group(2))) +
                        TimeUnit.SECONDS.toMillis(Long.parseLong(matcher.group(3)));
            }
        } else {
            return 0;
        }
    }

    public static boolean parseYesNo(Locale locale, String input) {
        JSONConfig language = Language.getLanguage(locale);
        if (language == null) {
            throw new IllegalArgumentException("Language file for locale " + locale.getLanguageCode() + " not found");
        }
        JsonArray yesWords = language.getArray("words.yes_words").orElse(new JsonArray());
        JsonArray noWords = language.getArray("words.no_words").orElse(new JsonArray());

        for (JsonElement yesWord : yesWords) {
            if (yesWord.isJsonPrimitive()) {
                if (input.equalsIgnoreCase(yesWord.getAsString())) {
                    return true;
                }
            }
        }

        for (JsonElement noWord : noWords) {
            if (noWord.isJsonPrimitive()) {
                if (input.equalsIgnoreCase(noWord.getAsString())) {
                    return false;
                }
            }
        }

        throw new IllegalArgumentException("No true/false match found for input " + input);
    }


}
