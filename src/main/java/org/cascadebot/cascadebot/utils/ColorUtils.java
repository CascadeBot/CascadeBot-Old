/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.utils;

import com.google.gson.JsonPrimitive;
import io.github.binaryoverload.JSONConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.cascadebot.cascadebot.CSSColor;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.data.objects.ColorErrorType;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;

import java.awt.Color;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtils {

    private static final Pattern HEX_COLOR = Pattern.compile("#([A-Fa-f0-9]+)");
    private static final Pattern DECIMAL_COLOR = Pattern.compile("([0-9]{1,8})");
    private static final Pattern RGB_COLOR = Pattern.compile("(\\d{1,3})[, ](\\d{1,3})[, ](\\d{1,3})");
    private static final Pattern BINARY_COLOR = Pattern.compile("([0-1]+)");


    public String getHex(int r, int g, int b) {
        return String.format("%02x%02x%02x", r, g, b);
    }

    public Color getColor(String text, CommandContext context) throws ColorException {

        Matcher matcher;

        String name = getColorNameFromLocale(context.getLocale(), text);
        Color color = getCssColor(name);

        if (color != null) {
            return color;
        }

        if ((matcher = RGB_COLOR.matcher(text)).find()) {
            try {
                return new Color(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)));
            } catch (IllegalArgumentException e) {
                throw new ColorException(ColorErrorType.RGB);
            }
        }

        if ((matcher = HEX_COLOR.matcher(text)).matches()) {
            try {
                return Color.decode(matcher.group());
            } catch (NumberFormatException e) {
                throw new ColorException(ColorErrorType.HEX);
            }
        }

        if ((matcher = DECIMAL_COLOR.matcher(text)).matches()) {
            try {
                return Color.decode(matcher.group());
            } catch (NumberFormatException e) {
                throw new ColorException(ColorErrorType.DECIMAL);
            }
        }

        if ((matcher = BINARY_COLOR.matcher(text)).matches()) {
            try {
                return Color.decode(String.valueOf(Integer.parseUnsignedInt(matcher.group(), 2)));
            } catch (NumberFormatException e) {
                throw new ColorException(ColorErrorType.BINARY);
            }
        }

        throw new ColorException(ColorErrorType.UNRECOGNISED);
    }

    public static MessageEmbed getColorEmbed(Color color, CommandContext context) {
        String rgbValues = color.getRed() + "," + color.getGreen() + "," + color.getBlue();
        String hex = "#" + ColorUtils.getHex(color.getRed(), color.getGreen(), color.getBlue());
        int decimalColor = color.getRGB();
        EmbedBuilder builder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, context.getUser());
        builder.setTitle(context.i18n("utils.color.embed_title", CSSColor.getLocalNameOrDefault(context.getLocale(), color, hex)));
        builder.setColor(color);
        builder.addField(context.i18n("utils.color.hex"), hex, true);
        builder.addField(context.i18n("utils.color.rgb"), rgbValues, true); // RGB Values
        builder.addField(context.i18n("utils.color.decimal"), Integer.toUnsignedString(decimalColor), true); // Decimal Value
        builder.addField(context.i18n("utils.color.binary"), Integer.toBinaryString(decimalColor), true); // Binary Value
        return builder.build();
    }

    @Getter
    @AllArgsConstructor
    public static class ColorException extends Exception {

        private final ColorErrorType type;

        public String getI18nMessage(Locale locale) {
            switch (type) {
                case RGB:
                    return Language.i18n(locale, "utils.color.invalid_rgb");
                case BINARY:
                    return Language.i18n(locale, "utils.color.invalid_binary");
                case HEX:
                    return Language.i18n(locale, "utils.color.invalid_hex");
                case DECIMAL:
                    return Language.i18n(locale, "utils.color.invalid_decimal");
                case UNRECOGNISED:
                    return Language.i18n(locale, "utils.color.color_not_recognised");
            }
            return null;
        }

    }

    // Gets a color name based on current locale, if not found return original input
    // This method needs a serious overhaul since I don't know how JSONConfig works...
    private static String getColorNameFromLocale(Locale locale, String name) {
        // Get language config
        JSONConfig lang = Language.getLanguage(locale);
        // Get sub config that defines the colors
        Optional<JSONConfig> sub = lang.getSubConfig("utils.color.colors");
        // If the sub config is not present return the name
        if (sub.isEmpty()) {
            return name;
        }

        // Get the sub config
        JSONConfig colorsLangConfig = sub.get();
        // Get all the values from the sub config
        Map<String, Object> colors = colorsLangConfig.getValues(false);

        // Loop over all values
        for (Map.Entry<String, Object> color : colors.entrySet()) {
            // Check if the value (color translation) is equal to the name provided
            if (((JsonPrimitive) color.getValue()).getAsString().equalsIgnoreCase(name)) {
                return color.getKey();
            }
        }
        // Return input when nothing has been found
        return name;
    }

    public static Color getCssColor(String name) {
        try {
            return CSSColor.valueOf(name.toUpperCase()).getColor();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
