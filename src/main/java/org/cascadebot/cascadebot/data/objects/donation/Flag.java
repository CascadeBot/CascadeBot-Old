package org.cascadebot.cascadebot.data.objects.donation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

@EqualsAndHashCode
public class Flag {

    @Getter
    private final String id;

    public Flag(String id) {
        this.id = id;
    }

    protected Flag() {
        this.id = null;
    }

    public String getName(Locale locale) {
        return Language.getLanguage(locale).getString("flags." + id + ".name").orElse("No language string defined");
    }

    public String getDescription(Locale locale) {
        return Language.getLanguage(locale).getString("flags." + id + ".description").orElse("No language string defined");
    }
}
