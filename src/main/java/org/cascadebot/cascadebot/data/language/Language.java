/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.language;

import com.ibm.icu.text.MessageFormat;
import io.github.binaryoverload.JSONConfig;
import net.dv8tion.jda.internal.utils.Checks;
import org.apache.commons.lang3.ArrayUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Language {

    private static final Language INSTANCE = new Language();

    private Map<Locale, JSONConfig> languages = new HashMap<>();

    private Language() {
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
            JSONConfig config = new JSONConfig(stream);
            config.setAllowedSpecialCharacters(ArrayUtils.add(config.getAllowedSpecialCharacters(), '#'));
            languages.put(locale, config);
        }
    }

    public static Map<Locale, JSONConfig> getLanguages() {
        return Map.copyOf(INSTANCE.languages);
    }

    public static JSONConfig getLanguage(Locale locale) {
        return INSTANCE.languages.get(locale);
    }

    public static boolean hasLanguageEntry(Locale locale, String path) {
        return INSTANCE.languages.containsKey(locale) && INSTANCE.languages.get(locale).getString(path).isPresent();
    }

    public static String i18n(long guildId, String path, Object... args) {
        Locale locale = getGuildLocale(guildId);
        if (INSTANCE.languages.containsKey(locale)) {
            if (INSTANCE.languages.get(locale).getString(path).isPresent()) {
                MessageFormat format = new MessageFormat(INSTANCE.languages.get(locale).getString(path).get(), locale.getULocale());
                String message = format.format(args);
                message = FormatUtils.formatPrefix(GuildDataManager.getGuildData(guildId).getCoreSettings().getPrefix(), message);
                return FormatUtils.formatUnicode(message);
            } else {
                CascadeBot.LOGGER.warn("Cannot find a language string matching the path '{}'", path);
                return INSTANCE.languages.get(Locale.getDefaultLocale()).getString(path).isPresent() ? i18n(Locale.getDefaultLocale(), path, args) : "No language string for " + path;
            }
        } else {
            throw new IllegalStateException("The language file matching locale '" + locale.getLanguageCode()
                    + "' does not exist or is not loaded!");
        }
    }

    public static Locale getGuildLocale(long guildId) {
        return GuildDataManager.getGuildData(guildId).getLocale();
    }

    public static String i18n(Locale locale, String path, Object... args) {
        Checks.notNull(locale, "locale");
        if (INSTANCE.languages.containsKey(locale)) {
            if (INSTANCE.languages.get(locale).getString(path).isPresent()) {
                MessageFormat format = new MessageFormat(INSTANCE.languages.get(locale).getString(path).get(), locale.getULocale());
                return FormatUtils.formatUnicode(format.format(args));
            } else {
                CascadeBot.LOGGER.warn("Cannot find a language string matching the path '{}'", path);
                return INSTANCE.languages.get(Locale.getDefaultLocale()).getString(path).isPresent() ? i18n(Locale.getDefaultLocale(), path, args) : "No language string for " + path;
            }
        } else {
            throw new IllegalStateException("The language file matching locale '" + locale.getLanguageCode()
                    + "' does not exist or is not loaded!");
        }
    }

}
