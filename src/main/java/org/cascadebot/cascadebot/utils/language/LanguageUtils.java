/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.language;

import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.utils.FormatUtils;

public class LanguageUtils {

    public static <T extends Enum<T>> String i18nEnum(T enumParam, Locale locale) {
        String path = "enums." + enumParam.getClass().getSimpleName().toLowerCase() + "." + enumParam.name().toLowerCase();
        if (!Language.hasLanguageEntry(locale, path)) {
            return FormatUtils.formatEnum(enumParam);
        }
        return StringUtils.capitalize(Language.i18n(locale, path));
    }

}
