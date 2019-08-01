/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects.donation;

import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

public class Flag {

    private String id;

    protected Flag() {
        //default constructor for mongo.
    }

    public Flag(String id) {
        this.id = id;
    }

    //TODO lang
    public String getName(Locale locale) {
        return Language.getLanguage(locale).getString("flags." + id + ".name").orElse("No language string defined");
    }

    public String getDescription(Locale locale) {
        return Language.getLanguage(locale).getString("flags." + id + ".description").orElse("No language string defined");
    }

}
