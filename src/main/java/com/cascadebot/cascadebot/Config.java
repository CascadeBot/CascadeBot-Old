/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.database.DatabaseManager;
import com.cascadebot.shared.ExitCodes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    public static Config INS;

    private File config;

    private String botToken;
    private Long botID;
    private HashMap<ICommandRestricted.CommandLevel, Long> commandLevels;

    private boolean prettyJson;
    private String defaultPrefix;
    private String hasteServer;
    private String hasteLink;

    private DatabaseManager.ConnectionStringType connectionStringType;



    private Config(String file) throws IOException {
        config = new File(file);
        initConfig();
    }

    private Config(File file) throws IOException {
        config = file;
        initConfig();
    }

    public static void init(String file) throws IOException {
        INS = new Config(file);
    }

    public static void init(File file) throws IOException {
        INS = new Config(file);
    }

    @SuppressWarnings("unchecked")
    private void initConfig() throws IOException {
        Yaml yaml = new Yaml();

        String configStr = FileUtils.readFileToString(this.config, Charset.defaultCharset());
        if (configStr.contains("\t")) {
            configStr = configStr.replace("\t", "  ");
            LOG.warn("{} contains a tab! Please look into replacing it with normal spaces!", this.config.getName());
        }

        Map<String, Object> config = yaml.load(configStr);

        this.botToken = (String) config.getOrDefault("token", "");
        if (this.botToken.isEmpty()) {
            LOG.error("No bot token provided in config! Please provide a token to start the bot.");
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
        }
        this.botID = (Long) config.getOrDefault("bot_id", -1);
        if (this.botID == -1) {
            LOG.error("No bot ID provided in config! Please provide the bot ID to start the bot.");
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
        }

        this.prettyJson = (boolean) config.getOrDefault("prettyJson", false);

        this.defaultPrefix = warnOnDefault(config, "default_prefix", ";");

        this.commandLevels = new HashMap<>();
        Object commandLevels = config.get("command_level");
        if (commandLevels instanceof Map) {
            Map<String, Object> levelMap = (Map<String, Object>) commandLevels;
            for (String s : levelMap.keySet()) {
                if (EnumUtils.isValidEnum(ICommandRestricted.CommandLevel.class, s.toUpperCase())) {
                    this.commandLevels.put(ICommandRestricted.CommandLevel.valueOf(s), (Long) levelMap.get(s));
                }
            }
        } else {

        }

        this.hasteServer = warnOnDefault(config,"haste_server", "https://hastebin.com/documents");
        this.hasteLink = warnOnDefault(config,"haste_link", "https://hastebin.com/");
    }

    @SuppressWarnings("unchecked")
    private <T> T warnOnDefault(Map<String, Object> config, String key, T defaultValue) {
        T object = (T) config.get(key);
        if (object == null) {
            LOG.warn("Value for key: {} was not provided! Using default value: \"{}\"", key, String.valueOf(defaultValue));
            return defaultValue;
        } else {
            return object;
        }
    }

    public String getBotToken() {
        return botToken;
    }

    public Long getBotID() {
        return botID;
    }

    public boolean isPrettyJson() {
        return prettyJson;
    }

    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    public HashMap<ICommandRestricted.CommandLevel, Long> getCommandLevels() {
        return commandLevels;
    }

    public String getHasteServer() {
        return hasteServer;
    }

    public String getHasteLink() {
        return hasteLink;
    }

}
