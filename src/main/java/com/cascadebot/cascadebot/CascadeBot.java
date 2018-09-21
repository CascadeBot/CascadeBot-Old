package com.cascadebot.cascadebot;

import com.cascadebot.cascadebot.commands.CommandManager;
import com.cascadebot.cascadebot.events.Events;
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

    private static Gson GSON;
    private static Logger LOGGER = LoggerFactory.getLogger(CascadeBot.class);

    private Config config;
    private JDA jda;
    private CommandManager commandManager;

    private static CascadeBot instance;

    public void init() {
        GsonBuilder builder = new GsonBuilder();
        try {
            config = new Config("config.yml");
        } catch (IOException e) {
            LOGGER.error("Error reading config file", e);
            System.exit(23);
            return;
        }

        if(Config.VALUES.prettyJson) {
            builder.setPrettyPrinting();
        }

        GSON = builder.create();
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(Config.VALUES.botToken).build();
        } catch (LoginException e) {
            LOGGER.error("Error building jda", e);
            System.exit(23);
            return;
        }

        jda.addEventListener(new Events());

        commandManager = new CommandManager();

        instance = this;
    }

    public static CascadeBot instance() {
        return instance;
    }

    public Logger getLogger() {
        return LOGGER;
    }
}
