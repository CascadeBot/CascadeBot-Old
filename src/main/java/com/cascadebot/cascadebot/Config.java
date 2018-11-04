/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.shared.ExitCodes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class Config {

    private File config;

    private List<String> required = new ArrayList<>();

    public static Values VALUES;

    public Config(String file) throws IOException {
        config = new File(file);
        initConfig();
    }

    public Config(File file) throws IOException {
        config = file;
        initConfig();
    }

    private void initConfig() throws IOException {
        required.addAll(Arrays.asList("bot.token", "bot.id")); //Add config path requirements here
        Yaml yaml = new Yaml();

        String configStr = FileUtils.readFileToString(this.config, Charset.defaultCharset());

        Map<String, Object> config = yaml.load(configStr);

        List<String> notMeet = new ArrayList<>();

        for (String req : required) {
            String[] path = req.split("\\.");
            if(!meetsRequirements(config, path)) {
                notMeet.add(req);
            }
        }

        if(notMeet.isEmpty()) {
            CascadeBot.instance().getLogger().error("Required config elements not meet");
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
            return;
        }

        VALUES = new Values();
        Object botInfo = config.get("bot");
        if (botInfo instanceof Map) {
            Map<String, Object> botMap = (Map<String, Object>) botInfo;
            VALUES.botToken = (String) botMap.get("token");
            VALUES.id = (Long) botMap.get("id");
        }

        VALUES.prettyJson = (boolean) config.getOrDefault("prettyJson", false);

        VALUES.defaultPrefix = (String) config.getOrDefault("prefix", ";");

        VALUES.commandLevels = new HashMap<>();
        Object commandLevels = config.get("commandLevel");
        if (commandLevels instanceof Map) {
            Map<String, Object> levelMap = (Map<String, Object>) commandLevels;
            for (String s : levelMap.keySet()) {
                if (EnumUtils.isValidEnum(ICommandRestricted.CommandLevel.class, s.toUpperCase())) {
                    VALUES.commandLevels.put(ICommandRestricted.CommandLevel.valueOf(s), (Long) levelMap.get(s));
                }
            }
        }

        VALUES.hasteServer = (String) config.getOrDefault("hasteServer", "https://hastebin.com/documents");
        VALUES.hasteLink = (String) config.getOrDefault("hasteLink", "https://hastebin.com/");
    }

    private boolean meetsRequirements(Map<String, Object> map, String[] path) {
        if(!map.containsKey(path[0])) {
            return false;
        } else {
            Object newMap = map.get(path[0]);
            if(newMap instanceof Map) {
                Map<String, Object> down = (Map<String, Object>) newMap;
                String[] newPath = Arrays.copyOfRange(path, 1, path.length);
                return meetsRequirements(down, newPath);
            } else return false;
        }
    }

    public class Values {
        public String botToken;
        public long id;

        public boolean prettyJson;

        public String defaultPrefix; // String to accommodate multiple char prefixes

        public long officalServer;

        public Map<ICommandRestricted.CommandLevel, Long> commandLevels;

        public String hasteServer;
        public String hasteLink;


    }
}
