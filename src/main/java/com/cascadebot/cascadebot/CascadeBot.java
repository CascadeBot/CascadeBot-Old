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
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.requests.RestAction;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.IOException;

public class CascadeBot {

    public static Logger logger = LoggerFactory.getLogger(CascadeBot.class);
    private static Gson gson;
    private static CascadeBot instance;

    private Config config;
    private ShardManager shardManager;
    private CommandManager commandManager;
    private OkHttpClient httpClient;

    public static void main(String[] args) {
        (instance = new CascadeBot()).init();
    }
    
    /**
     *  Runs once all shards are loaded
     */
    public void run() {
        logger.info("All shards successfully logged in!");
    }

    public void init() {
        instance = this;
        GsonBuilder builder = new GsonBuilder();
        try {
            config = new Config("config.yml");
        } catch (IOException e) {
            logger.error("Error reading config file", e);
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
            return;
        }

        httpClient = new OkHttpClient.Builder().build();

        if(Config.VALUES.prettyJson) {
            builder.setPrettyPrinting();
        }

        gson = builder.create();
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
            logger.error("Error building JDA", e);
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
            return;
        }

        commandManager = new CommandManager();

        Thread.setDefaultUncaughtExceptionHandler(((t, e) -> logger.error("Uncaught exception in thread " + t, e)));
        Thread.currentThread()
                .setUncaughtExceptionHandler(((t, e) -> logger.error("Uncaught exception in thread " + t, e)));

        RestAction.DEFAULT_FAILURE= throwable -> {
            logger.error("Uncaught exception in rest action", throwable);
        };

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

    /**
     * This  will return the first connected JDA shard.
     * This means that a lot of methods like sending embeds works even with shard 0 offline.
     *
     * @return The first possible JDA shard which is connected.
     */
    @Nonnull
    public JDA getClient() {
        for (JDA jda : shardManager.getShardCache()) {
            if (jda.getStatus() == JDA.Status.LOADING_SUBSYSTEMS || jda.getStatus() == JDA.Status.CONNECTED)
                return jda;
        }
        throw new IllegalStateException("getClient was called when no shards were connected!");
    }

    /**
     * Get the SelfUser of the bot, this will be returned from the first connected shard.
     *
     * @return The bot SelfUser from the first connected shard.
     */
    @Nonnull
    public SelfUser getSelfUser() {
        return getClient().getSelfUser();
    }

    public Logger getLogger() {
        return logger;
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
        return gson;
    }

}
