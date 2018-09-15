package com.cascadebot.cascadebot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class CascadeBot {
    public static void main(String[] args) {
        new CascadeBot().init();
    }

    private Gson gson;
    private Config config;
    private JDA jda;

    private static CascadeBot instance;

    private Logger logger = LoggerFactory.getLogger(CascadeBot.class);

    public void init() {
        GsonBuilder builder = new GsonBuilder();
        try {
            config = new Config("config.yml");
        } catch (IOException e) {
            logger.error("Error reading config file", e);
            System.exit(23);
            return;
        }

        if(Config.VALUES.prettyJson) {
            builder.setPrettyPrinting();
        }

        gson = builder.create();
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(Config.VALUES.botToken).build();
        } catch (LoginException e) {
            logger.error("Error building jda", e);
            System.exit(23);
            return;
        }

        jda.addEventListener(new Events());

        instance = this;
    }

    public static CascadeBot getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }
}
