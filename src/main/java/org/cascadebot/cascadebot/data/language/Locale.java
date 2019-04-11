/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.language;

import org.cascadebot.cascadebot.CascadeBot;

import java.io.InputStream;

public enum Locale {

    EN_UK("English (UK)"),
    EN_US("English (US)");

    private String displayName;

    Locale(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLanguageCode() {
        return name().toLowerCase();
    }

    public InputStream getLanguageFile() {
        return CascadeBot.class.getClassLoader().getResourceAsStream("lang/" + getLanguageCode() + ".yml");
    }

    public static Locale getDefaultLocale() {
        return EN_UK;
    }

}
