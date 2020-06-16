/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.binaryoverload.JSONConfig;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

import java.util.Optional;

public class LanguageUtils {

    // TODO: Doc
    public static <T extends Enum> String getEnumI18n(Locale locale, String base, T enumToGet) {
        return Language.i18n(locale, base + "." +  enumToGet.name().toLowerCase());
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
