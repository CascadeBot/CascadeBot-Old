package com.cascadebot.cascadebot;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Config {
    File config;

    List<String> required = new ArrayList<String>();

    public Config(String file) throws IOException {
        config = new File(file);
        initConfig();
    }

    public Config(File file) throws IOException {
        config = file;
        initConfig();
    }

    public void initConfig() throws IOException {
        required.addAll(Arrays.asList("bot.token", "bot.id"));
        Yaml yaml = new Yaml();

        String configStr = FileUtils.readFileToString(this.config, Charset.defaultCharset());

        Map<String, Object> config = yaml.load(configStr);

        List<String> notMeet = new ArrayList<>();

        for (String req : required) {
            String[] path = req.split("\\.");
            if(!checkReqMeat(config, path)) {
                notMeet.add(req);
            }
        }

        if(notMeet.isEmpty()) {
            //TODO logger stuffs
            System.out.println("Required config elements not meet");
        }
    }

    private boolean checkReqMeat(Map<String, Object> map, String[] path) {
        if(!map.containsKey(path[0])) {
            return false;
        } else {
            Map<String, Object> down = (Map<String, Object>) map.get(path[0]);
            String[] newPath = Arrays.copyOfRange(path, 1, path.length - 1);
            return checkReqMeat(down, newPath);
        }
    }
}
