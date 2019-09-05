package org.cascadebot.cascadebot.data.objects.donation;

import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

public interface IFlag {
    String id = null;

    default public String getName(Locale locale) {
        return Language.getLanguage(locale).getString("flags." + id + ".name").orElse("No language string defined");
    }

    default public String getId() {
        return id;
    }

    default public String getDescription(Locale locale) {
        return Language.getLanguage(locale).getString("flags." + id + ".description").orElse("No language string defined");
    }
}
