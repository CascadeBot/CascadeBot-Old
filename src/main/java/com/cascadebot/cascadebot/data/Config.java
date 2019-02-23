/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data;

import ch.qos.logback.classic.Level;
import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.ShutdownHandler;
import com.cascadebot.cascadebot.music.MusicHandler;
import com.cascadebot.cascadebot.utils.LogbackUtils;
import com.cascadebot.shared.Auth;
import com.cascadebot.shared.SecurityLevel;
import com.google.common.collect.HashMultimap;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    public static Config INS;

    private File config;

    private boolean debug;

    private Auth auth;

    private String botToken;
    private Long botID;
    private HashMultimap<SecurityLevel, Long> securityLevels;

    private Map<String, Long> globalEmotes;

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

    private long officialServerId;

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
        LOG.info("Starting to load configuration!");

        FileConfiguration config = new YamlConfiguration();

        try {
            config.load(this.config);
        } catch (InvalidConfigurationException e) {
            LOG.error("Invalid yaml configuration", e);
            ShutdownHandler.exitWithError();
            return;
        }

        this.debug = config.getBoolean("debug", false);
        if (this.debug) {
            LOG.info("Debug mode enabled!");
            LogbackUtils.setAppenderLevel("STDOUT", Level.DEBUG);
            LogbackUtils.setLoggerLevel("org.mongodb.driver.cluster", Level.DEBUG);
        }

        this.botID = config.getLong("bot.id", -1);
        if (this.botID == -1) {
            LOG.error("No bot ID provided in config! Please provide the bot ID to start the bot.");
            ShutdownHandler.exitWithError();
        }

        this.botToken = config.getString("bot.token", "");
        if (this.botToken.isEmpty()) {
            LOG.error("No bot token provided in config! Please provide a token to start the bot.");
            ShutdownHandler.exitWithError();
        }

        if (!config.contains("database")) {
            LOG.error("No database info provided, exiting!");
            ShutdownHandler.exitWithError();
            return;
        }

        if (config.contains("database.connection_string") && !config.getString("database.connection_string").isBlank()) {
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
                ShutdownHandler.exitWithError();
            }
            this.ssl = warnOnDefault(config, "database.ssl", false);
        }

        shardNum = warnOnDefault(config, "shard_num", -1);

        if (config.contains("official_server")) {
            officialServerId = config.getLong("official_server");
        } else {
            LOG.warn("There is no official server specified! Role checking will not work!");
            officialServerId = -1L;
        }

        this.prettyJson = config.getBoolean("pretty_json", false);

        this.defaultPrefix = warnOnDefault(config, "default_prefix", ";");

        this.securityLevels = HashMultimap.create();
        ConfigurationSection configSecurityLevels = config.getConfigurationSection("security_levels");
        if (configSecurityLevels != null) {
            for (String level : configSecurityLevels.getKeys(false)) {
                if (EnumUtils.isValidEnum(SecurityLevel.class, level.toUpperCase())) {
                    SecurityLevel securityLevel = SecurityLevel.valueOf(level.toUpperCase());
                    Object value = configSecurityLevels.get(level);
                    if (value instanceof List) {
                        for (Long id : (List<Long>) value) {
                            this.securityLevels.put(securityLevel, id);
                        }
                    } else if (value instanceof Long) {
                        this.securityLevels.put(securityLevel, (Long) value);
                    }
                }
            }
        } else {
            LOG.error("Please define security levels in your config! Without these, you won't be able to run privileged commands!");
            ShutdownHandler.exitWithError();
        }

        this.globalEmotes = new HashMap<>();
        ConfigurationSection configGlobalEmotes = config.getConfigurationSection("global_emotes");
        if (configGlobalEmotes != null) {
            for (String emoteKey : configGlobalEmotes.getKeys(false)) {
                Long emoteId = configGlobalEmotes.getLong(emoteKey);
                if (emoteId > 0) {
                    this.globalEmotes.put(emoteKey, emoteId);
                }
            }
        }

        this.hasteServer = warnOnDefault(config, "haste.server", "https://hastebin.com/documents");
        this.hasteLink = warnOnDefault(config, "haste.link", "https://hastebin.com/");

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

        String secret = warnOnDefault(config, "web.secret_key", "");

        try {
            auth = new Auth(secret);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            CascadeBot.LOGGER.warn("Auth failed to initiate. this might cause errors if working with he wrapper or website if the bot is working with those.", e);
        }

        LOG.info("Finished loading configuration!");
        LOG.debug("Configuration: {}", new GsonBuilder().create().toJson(this)); // Need to create new GSON as global GSON hasn't been build yet

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

    public HashMultimap<SecurityLevel, Long> getSecurityLevels() {
        return securityLevels;
    }

    public Map<String, Long> getGlobalEmotes() {
        return globalEmotes;
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

    public long getOfficialServerId() {
        return officialServerId;
    }

    public Auth getAuth() {
        return auth;
    }

}
