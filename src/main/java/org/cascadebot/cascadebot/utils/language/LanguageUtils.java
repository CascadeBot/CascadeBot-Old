/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.language;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LanguageUtils {

    public static <T extends Enum<T>> String i18nEnum(T enumParam, Locale locale) {
        String path = "enums." + enumParam.getClass().getSimpleName().toLowerCase() + "." + enumParam.name().toLowerCase();
        if (!Language.hasLanguageEntry(locale, path)) {
            return FormatUtils.formatEnum(enumParam);
        }
        return StringUtils.capitalize(Language.i18n(locale, path));
    }

    public static <T extends Enum<T>> String getListValidEnum(Class<T> enumClass, Locale locale, Predicate<T> filter) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(filter)
                .map(theEnum -> "`" + FormatUtils.formatEnum(theEnum, locale) + "`")
                .collect(Collectors.joining(", "));
    }

    public static <T extends Enum<T>> String getListValidEnum(Class<T> enumClass, Locale locale) {
        return getListValidEnum(enumClass, locale, e -> true);
    }

    public static <T extends Enum<T>> T findEnumByI18n(Class<T> enumClass, Locale locale, String search, boolean ignoreCase) {
        var language = Language.getLanguage(locale);
        if (language == null) return null;
        var jsonElement = language.getElement("enums." + enumClass.getSimpleName().toLowerCase());

        if (jsonElement.isPresent()) {
            if (!jsonElement.get().isJsonObject()) {
                return null;
            }
            var result = jsonElement.get().getAsJsonObject().entrySet().stream().filter(entry -> {
                if (!entry.getValue().isJsonPrimitive()) return false;
                if (ignoreCase) {
                    return entry.getValue().getAsString().equalsIgnoreCase(search);
                } else {
                    return entry.getValue().getAsString().equals(search);
                }
            }).findFirst();

            if (result.isEmpty()) {
                return null;
            }

            return EnumUtils.getEnumIgnoreCase(enumClass, result.get().getKey());
        }

        return null;
    }

}
