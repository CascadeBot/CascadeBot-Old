/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.language;

import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

public class LanguageUtils {

    // TODO: Doc
    public static <T extends Enum> String getEnumI18n(Locale locale, String base, T enumToGet) {
        return Language.i18n(locale, base + "." +  enumToGet.name().toLowerCase());
    }

}
