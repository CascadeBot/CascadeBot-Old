/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.moderation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cascadebot.cascadebot.CascadeBot;
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

    public String getVerb() {
        return verb;
    }

    public String getVerb(Locale locale) {
        String path = "mod_actions." + name().toLowerCase() + ".verb";
        if (!CascadeBot.INS.getLanguage().hasLanguageEntry(locale, path)) {
            return getVerb();
        }
        return CascadeBot.INS.getLanguage().get(locale, path);
    }

    public String getName() {
        return name().toLowerCase().replace("_", "-");
    }

    public String getName(Locale locale) {
        String path = "mod_actions." + name().toLowerCase() + ".name";
        if (!CascadeBot.INS.getLanguage().hasLanguageEntry(locale, path)) {
            return getName();
        }
        return CascadeBot.INS.getLanguage().get(locale, path);
    }

}
