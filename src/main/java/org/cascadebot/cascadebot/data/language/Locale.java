/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.language;

import com.ibm.icu.util.ULocale;
import org.cascadebot.cascadebot.CascadeBot;

import java.io.InputStream;

public enum Locale {

    ENGLISH_UK(ULocale.UK),
    ENGLISH_US(ULocale.US);

    private ULocale locale;

    Locale(ULocale locale) {
        this.locale = locale;
    }

    public String getDisplayName() {
        return getDisplayName(false);
    }

    public String getDisplayName(boolean localised) {
        if (localised) return locale.getDisplayName(locale);
        return locale.getDisplayName();
    }

    public String getLanguageCode() {
        return locale.toLanguageTag();
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public String getLanguageFileName() {
        return getLanguageCode() + ".json";
    }

    public InputStream getLanguageFile() {
        return CascadeBot.class.getClassLoader().getResourceAsStream("lang/" + getLanguageFileName());
    }

    public String i18n(String path, Object... objects) {
        return Language.i18n(this, path, objects);
    }

    public static Locale getDefaultLocale() {
        return ENGLISH_UK;
    }

    public ULocale getULocale() {
        return locale;
    }
}
