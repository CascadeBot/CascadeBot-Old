package org.cascadebot.cascadebot.data.objects.donation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

@EqualsAndHashCode
public class Flag {

    @Getter
    protected final String id;
    @Getter
    protected final FlagScope scope;

    public Flag(String id, FlagScope scope) {
        this.id = id;
        this.scope = scope;
    }

    protected Flag() {
        this.id = null;
        this.scope = null;
    }

    public String getName(Locale locale) {
        return Language.getLanguage(locale).getString("flags." + id + ".name").orElse("No language string defined");
    }

    public String getDescription(Locale locale) {
        return Language.getLanguage(locale).getString("flags." + id + ".description").orElse("No language string defined");
    }

    public enum FlagScope {

        USER,
        GUILD

    }
}
