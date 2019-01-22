/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.cascadebot.commandmeta.CommandManager;
import com.cascadebot.cascadebot.data.Config;
import com.cascadebot.cascadebot.data.database.DatabaseManager;
import com.cascadebot.cascadebot.events.ButtonEventListener;
import com.cascadebot.cascadebot.events.CommandListener;
import com.cascadebot.cascadebot.events.Events;
import com.cascadebot.cascadebot.music.MusicHandler;
import com.cascadebot.cascadebot.permissions.PermissionsManager;
import com.cascadebot.shared.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.requests.RestAction;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Scanner;

public class CascadeBot {

    public static final CascadeBot INS = new CascadeBot();

    public static Logger logger = LoggerFactory.getLogger(CascadeBot.class);
    private static Version version;
    private static Gson gson;

    private ShardManager shardManager;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private PermissionsManager permissionsManager;
    private OkHttpClient httpClient;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(CascadeBot.class.getResourceAsStream("/version.txt"))) {
            version = Version.parseVer(scanner.next());
        }
        INS.init();
    }

    public static Version getVersion() {
        return version;
    }

    public static Gson getGSON() {
        return gson;
    }

    public static String getInvite() {
        return String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=%s",
                CascadeBot.INS.getSelfUser().getId(), Permission.ALL_GUILD_PERMISSIONS);
    }

    /**
     *  Runs once all shards are loaded
     */
    public void run() {
        logger.info("All shards successfully logged in!");
        logger.info("Cascade Bot version {} successfully booted up!", version);
    }



    private void init() {
        new Thread(new ConsoleReader()).start();

        GsonBuilder builder = new GsonBuilder();
        try {
            Config.init("config.yml");
        } catch (IOException e) {
            logger.error("Error reading config file", e);
            ShutdownHandler.exitWithError();
            return;
        }

        httpClient = new OkHttpClient.Builder().build();

        if (Config.INS.isPrettyJson()) {
            builder.setPrettyPrinting();
        }

        if (Config.INS.getConnectionString() != null) {
            databaseManager = new DatabaseManager(Config.INS.getConnectionString());
        } else {
            databaseManager = new DatabaseManager(
                    Config.INS.getUsername(),
                    Config.INS.getPassword(),
                    Config.INS.getDatabase(),
                    Config.INS.getHosts(),
                    Config.INS.isSsl()
            );
        }

        JdaLavalink lavalink = new MusicHandler(this).buildMusic();

        gson = builder.create();
        try {
            shardManager = new DefaultShardManagerBuilder()
                    .addEventListeners(new CommandListener())
                    .addEventListeners(new Events())
                    .addEventListeners(new ButtonEventListener())
                    .addEventListeners(lavalink)
                    .setToken(Config.INS.getBotToken())
                    //.setAudioSendFactory(new NativeAudioSendFactory())
                    .setShardsTotal(-1)
                    .setGameProvider(shardId -> {
                        if (version.getBuild().equalsIgnoreCase("dev")) {
                            return Game.streaming(" the devs mistakes", "https://twitch.tv/someone");
                        } else {
                            return Game.playing("CascadeBot Version " + version);
                        }
                    })
                    .setBulkDeleteSplittingEnabled(false)
                    .setEnableShutdownHook(false)
                    .build();
        } catch (LoginException e) {
            logger.error("Error building JDA", e);
            ShutdownHandler.exitWithError();
            return;
        }

        commandManager = new CommandManager();
        permissionsManager = new PermissionsManager();

        Thread.setDefaultUncaughtExceptionHandler(((t, e) -> logger.error("Uncaught exception in thread " + t, e)));
        Thread.currentThread()
                .setUncaughtExceptionHandler(((t, e) -> logger.error("Uncaught exception in thread " + t, e)));

        RestAction.DEFAULT_FAILURE = throwable -> {
            logger.error("Uncaught exception in rest action", throwable);
        };

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

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

}
