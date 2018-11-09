/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.music.MusicHandler;
import com.cascadebot.shared.ExitCodes;
import com.google.common.collect.HashMultimap;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    public static Config INS;

    private File config;

    private String botToken;
    private Long botID;
    private HashMultimap<ICommandRestricted.CommandLevel, Long> commandLevels;

    private boolean prettyJson;
    private String defaultPrefix;
    private String hasteServer;
    private String hasteLink;

    private String username;
    private char[] password;
    private String database;
    private List<String> hosts;
    private boolean ssl;

    private String connectionString;

    private int shardNum;

    private List<MusicHandler.MusicNode> musicNodes = new ArrayList<>();

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
        FileConfiguration config = new YamlConfiguration(); //TODO create own file config based off of spigots

        //TODO switch over to nice methods for yaml

        try {
            config.load(this.config);
        } catch (InvalidConfigurationException e) {
            LOG.error("Invalid yaml configuration", e);
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
            return;
        }

        this.botToken = config.getString("bot.token", "");
        if (this.botToken.isEmpty()) {
            LOG.error("No bot token provided in config! Please provide a token to start the bot.");
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
        }

        this.botID = config.getLong("bot.id", -1);
        if (this.botID == -1) {
            LOG.error("No bot ID provided in config! Please provide the bot ID to start the bot.");
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
        }

        this.prettyJson = config.getBoolean("prettyJson", false);

        this.defaultPrefix = warnOnDefault(config, "default_prefix", ";");

        this.commandLevels = HashMultimap.create();
        Object commandLevels = config.get("command_level");
        if (commandLevels instanceof Map) {
            Map<String, Object> levelMap = (Map<String, Object>) commandLevels;
            for (String s : levelMap.keySet()) {
                if (EnumUtils.isValidEnum(ICommandRestricted.CommandLevel.class, s.toUpperCase())) {
                    this.commandLevels.put(ICommandRestricted.CommandLevel.valueOf(s), (Long) levelMap.get(s));
                }
            }
        } else {
            // TODO: What to do here?
            // weeryan17's thoughts: do nothing but log that it's invalid
        }

        this.hasteServer = warnOnDefault(config, "haste_server", "https://hastebin.com/documents");
        this.hasteLink = warnOnDefault(config, "haste_link", "https://hastebin.com/");

        if (!config.contains("database")) {
            LOG.error("No database info provided, exiting!");
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
            return;
        }

        if (config.contains("database.connection_string")) {
            this.connectionString = config.getString("database.connection_string");
        } else {
            this.username = config.getString("database.username");
            var passwordTemp = config.getString("database.password");
            if (passwordTemp != null) {
                this.password = passwordTemp.toCharArray();
            }
            this.database = config.getString("database.database");
            this.hosts = config.getStringList("database.hosts");
            if (this.hosts.size() == 0 || this.hosts.stream().allMatch(String::isBlank)) {
                LOG.error("There are no valid hosts specified, exiting!");
                System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
            }
            this.ssl = warnOnDefault(config, "database.ssl", false);
        }

        shardNum = warnOnDefault(config, "shards", -1);

        if (config.contains("nodes")) {
            List<Map<?, ?>> rawNodes = config.getMapList("nodes");
            for (Map<?, ?> rawNode : rawNodes) {
                String address = (String) rawNode.get("address");
                String password = (String) rawNode.get("password");
                try {
                    musicNodes.add(new MusicHandler.MusicNode(new URI(address), password));
                } catch (URISyntaxException e) {
                    LOG.warn("Invalid url for node provided", e);
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    private <T> T warnOnDefault(FileConfiguration config, String path, T defaultValue) {
        T object = (T) config.get(path);
        if (object == null) {
            LOG.warn("Value for key: {} was not provided! Using default value: \"{}\"", path, String.valueOf(defaultValue));
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

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public boolean isSsl() {
        return ssl;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public int getShardNum() {
        return shardNum;
    }

    public List<MusicHandler.MusicNode> getMusicNodes() {
        return musicNodes;
    }
}
