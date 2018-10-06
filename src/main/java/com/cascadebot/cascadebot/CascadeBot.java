/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.cascadebot.commandmeta.CommandManager;
import com.cascadebot.cascadebot.commands.developer.EvalCommand;
import com.cascadebot.cascadebot.events.CommandListener;
import com.cascadebot.cascadebot.events.Events;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class CascadeBot {

    public static void main(String[] args) {
        new CascadeBot().init();
    }

    private static Gson GSON;
    public static Logger LOGGER = LoggerFactory.getLogger(CascadeBot.class);

    private Config config;
    private ShardManager shardManager;
    private CommandManager commandManager;

    private static CascadeBot instance;

    private OkHttpClient httpClient;

    /**
     *  Runs once all shards are loaded
     */
    public void run() {
        LOGGER.info("All shards successfully logged in!");
    }

    public void init() {
        instance = this;
        GsonBuilder builder = new GsonBuilder();
        try {
            config = new Config("config.yml");
        } catch (IOException e) {
            LOGGER.error("Error reading config file", e);
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
            return;
        }

        httpClient = new OkHttpClient.Builder().build();

        if(Config.VALUES.prettyJson) {
            builder.setPrettyPrinting();
        }

        GSON = builder.create();
        try {
            shardManager = new DefaultShardManagerBuilder()
                    .addEventListeners(new CommandListener())
                    .addEventListeners(new Events())
                    .setToken(Config.VALUES.botToken)
                    //.setAudioSendFactory(new NativeAudioSendFactory())
                    .setShardsTotal(-1)
                    //.setGameProvider(shardId -> Game)
                    .setBulkDeleteSplittingEnabled(false)
                    .build();
        } catch (LoginException e) {
            LOGGER.error("Error building JDA", e);
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
            return;
        }

        commandManager = new CommandManager();

        Thread.setDefaultUncaughtExceptionHandler(((t, e) -> LOGGER.error("Uncaught exception in thread " + t, e)));
        Thread.currentThread()
                .setUncaughtExceptionHandler(((t, e) -> LOGGER.error("Uncaught exception in thread " + t, e)));
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

    }

    public void shutdown() {
        EvalCommand.shutdownEvalPool();
        CommandListener.shutdownCommandPool();
        shardManager.shutdown();
    }

    public static CascadeBot instance() {
        return instance;
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public static Gson getGSON() {
        return GSON;
    }

}
