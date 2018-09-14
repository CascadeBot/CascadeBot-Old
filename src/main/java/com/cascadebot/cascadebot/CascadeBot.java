package com.cascadebot.cascadebot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class CascadeBot {
    public static void main(String[] args) {
        new CascadeBot().init();
    }

    private Gson gson;
    private Config config;
    private JDA jda;

    public void init() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            config = new Config("config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            jda = new JDABuilder(AccountType.BOT).setToken("").build();
        } catch (LoginException e) {
            e.printStackTrace();
        }


    }
}
