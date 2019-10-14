package org.cascadebot.cascadebot.utils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {

    private static final Pattern TIME_PARSER = Pattern.compile("(?<hours>\\d+)h|(?<minutes>\\d+)m|(?<seconds>\\d+)s");

    public static long parseTime(String input, boolean onlyAllowOnce) {
        Matcher matcher = TIME_PARSER.matcher(input);

        // These booleans represent whether the unit of time has been found yet
        boolean hoursParsed = false;
        boolean minutesParsed = false;
        boolean secondsParsed = false;

        // The result in milliseconds
        long millis = 0;
        while (matcher.find()) {
            if (matcher.group("hours") != null) {
                if (hoursParsed && onlyAllowOnce) {
                    throw new IllegalArgumentException("Only one of each time unit from the list: h, m, s is allowed!");
                }
                hoursParsed = true;
                millis += TimeUnit.HOURS.toMillis(Long.parseLong(matcher.group("hours")));
            } else if (matcher.group("minutes") != null) {
                if (minutesParsed && onlyAllowOnce) {
                    throw new IllegalArgumentException("Only one of each time unit from the list: h, m, s is allowed!");
                }
                minutesParsed = true;
                millis += TimeUnit.MINUTES.toMillis(Long.parseLong(matcher.group("minutes")));
            } else if (matcher.group("seconds") != null) {
                if (secondsParsed && onlyAllowOnce) {
                    throw new IllegalArgumentException("Only one of each time unit from the list: h, m, s is allowed!");
                }
                secondsParsed = true;
                millis += TimeUnit.SECONDS.toMillis(Long.parseLong(matcher.group("seconds")));
            }
        }
        return millis;
    }

}
