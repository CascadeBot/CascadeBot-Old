/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.language;

import com.ibm.icu.text.MessageFormat;
import io.github.binaryoverload.JSONConfig;
import net.dv8tion.jda.core.utils.Checks;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Language {

    private Map<Locale, JSONConfig> languages = new HashMap<>();

    public void initLanguage() {
        try {
            loadLanguage(Locale.ENGLISH_UK);
        } catch (Exception e) {
            CascadeBot.LOGGER.error("Could not load language!", e);
            ShutdownHandler.exitWithError();
        }
        CascadeBot.LOGGER.info("Loaded {} languages!", languages.size());
    }

    private void loadLanguage(Locale locale) {
        InputStream stream = locale.getLanguageFile();
        if (stream == null) {
            if (locale == Locale.getDefaultLocale()) {
                CascadeBot.LOGGER.error("I couldn't load the default language file {} from the JAR file, stopping the bot!", locale.getLanguageFileName());
                ShutdownHandler.exitWithError();
            } else {
                CascadeBot.LOGGER.warn("I couldn't load the language file {} from the JAR file!", locale.getLanguageFileName());
            }
        } else {
            languages.put(locale, new JSONConfig(stream));
        }
    }

    public JSONConfig getLanguage(Locale locale) {
        return languages.get(locale);
    }

    public boolean hasLanguageEntry(Locale locale, String path) {
        return languages.containsKey(locale) && languages.get(locale).getString(path).isPresent();
    }

    public String get(long guildId, String path, Object... args) {
        return get(GuildDataManager.getGuildData(guildId).getLocale(), path, args);
    }

    public String get(Locale locale, String path, Object... args) {
        Checks.notNull(locale, "locale");
        if (languages.containsKey(locale)) {
            if (languages.get(locale).getString(path).isPresent()) {
                MessageFormat format = new MessageFormat(languages.get(locale).getString(path).get(), locale.getULocale());
                return FormatUtils.formatUnicode(format.format(args));
            } else {
                CascadeBot.LOGGER.warn("Cannot find a language string matching the path '{}'", path);
                return languages.get(Locale.getDefaultLocale()).getString(path).isPresent() ? get(Locale.getDefaultLocale(), path, args) : "No language string for " + path;
            }
        } else {
            throw new IllegalStateException("The language file matching locale '" + locale.getLanguageCode()
                    + "' does not exist or is not loaded!");
        }
    }

}
