/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.moderation;

import lombok.AllArgsConstructor;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

@AllArgsConstructor
public enum ModAction {

    BAN("banned"), // Bans a user
    UNBAN("unbanned"), // Unbans a user
    SOFT_BAN("soft-banned"), // Bans a user then unbans them, clears messages without actually banning them
    FORCE_BAN("forcefully banned"), // Bans a user in a guild, even if the user isn't in the guild
    KICK("kicked"), // Kicks a user from a guild
    MUTE("muted"), // Mutes a user in a guild
    WARN("warned"); // Warns a user in a guild

    private String verb;

    // Only the localised version should be used
    private String getVerb() {
        return verb;
    }

    public String getVerb(Locale locale) {
        String path = "mod_actions." + name().toLowerCase() + ".verb";
        if (!Language.hasLanguageEntry(locale, path)) {
            return getVerb();
        }
        return Language.i18n(locale, path);
    }

    // Only the localised version should be used
    private String getName() {
        return name().toLowerCase().replace("_", "-");
    }

    public String getName(Locale locale) {
        String path = "mod_actions." + name().toLowerCase() + ".name";
        if (!Language.hasLanguageEntry(locale, path)) {
            return getName();
        }
        return Language.i18n(locale, path);
    }

}
