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

    BAN("banned", true), // Bans a user
    UNBAN("unbanned", false), // Unbans a user
    SOFT_BAN("soft-banned", true), // Bans a user then unbans them, clears messages without actually banning them
    FORCE_BAN("forcefully banned", false), // Bans a user in a guild, even if the user isn't in the guild
    KICK("kicked", true), // Kicks a user from a guild
    MUTE("muted", true), // Mutes a user in a guild
    WARN("warned", true), // Warns a user in a guild
    TEMP_MUTE("temporarily muted", true),
    TEMP_BAN("temporarily banned", true);
    // TODO: Temp command ban?

    private String verb;
    private boolean requiresMember;

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

    public boolean doesRequireMember() {
        return requiresMember;
    }
}
