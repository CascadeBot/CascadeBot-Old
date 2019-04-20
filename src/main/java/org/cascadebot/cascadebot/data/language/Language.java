/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.language;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Language {

    private static final Pattern PLACEHOLDER_REGEX = Pattern.compile("\\{.*\\}");

    private Map<Locale, YamlConfiguration> languages = new HashMap<>();

    public void initLanguage() {
        try {
            loadLanguage(Locale.EN_UK);
        } catch (InvalidConfigurationException | IOException e) {
            CascadeBot.LOGGER.error("Could not load language!", e);
            ShutdownHandler.exitWithError();
        }
        CascadeBot.LOGGER.info("Loaded {} languages!", languages.size());
    }

    private void loadLanguage(Locale locale) throws IOException, InvalidConfigurationException {
        InputStream stream = locale.getLanguageFile();
        if (stream == null) {
            if (locale == Locale.getDefaultLocale()) {
                CascadeBot.LOGGER.error("I couldn't load the default language file {}.yml from the JAR file, stopping the bot!", locale.getLanguageCode());
                ShutdownHandler.exitWithError();
            } else {
                CascadeBot.LOGGER.warn("I couldn't load the language file {}.yml from the JAR file!", locale.getLanguageCode());
            }
        } else {
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(new InputStreamReader(stream));
            languages.put(locale, configuration);
        }
    }

    public YamlConfiguration getLanguage(Locale locale) {
        return languages.get(locale);
    }

    public boolean hasLanguageEntry(Locale locale, String path) {
        return languages.containsKey(locale) && languages.get(locale).getString(path) != null;
    }

    public String get(Locale locale, String path, Object... args) {
        if (languages.containsKey(locale)) {
            if (languages.get(locale).getString(path) != null) {
                Matcher matcher = PLACEHOLDER_REGEX.matcher(languages.get(locale).getString(path));
                AtomicInteger count = new AtomicInteger(0);
                return matcher.replaceAll((matchResult -> {
                    if (count.get() >= args.length) return matchResult.group();
                    return String.valueOf(args[count.getAndIncrement()]);
                }));
            } else {
                CascadeBot.LOGGER.warn("Cannot find a language string matching the path '{}'", path);
                return "No language string for " + path;
            }
        } else {
            throw new IllegalStateException("The language file matching locale '" + locale.getLanguageCode()
                    + "' does not exist or is not loaded!");
        }
    }

}
