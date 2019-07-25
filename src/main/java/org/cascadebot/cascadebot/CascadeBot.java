/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.jackson.JacksonJsonFormatter;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import lavalink.client.io.jda.JDAVoiceInterceptor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.commandmeta.ArgumentManager;
import org.cascadebot.cascadebot.commandmeta.CommandManager;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.database.DatabaseManager;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.events.ButtonEventListener;
import org.cascadebot.cascadebot.events.CommandListener;
import org.cascadebot.cascadebot.events.GeneralEventListener;
import org.cascadebot.cascadebot.events.JDAEventMetricsListener;
import org.cascadebot.cascadebot.events.VoiceEventListener;
import org.cascadebot.cascadebot.metrics.Metrics;
import org.cascadebot.cascadebot.moderation.ModerationManager;
import org.cascadebot.cascadebot.music.MusicHandler;
import org.cascadebot.cascadebot.permissions.PermissionsManager;
import org.cascadebot.cascadebot.tasks.Task;
import org.cascadebot.cascadebot.utils.EventWaiter;
import org.cascadebot.cascadebot.utils.LogbackUtils;
import org.cascadebot.shared.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class CascadeBot {

    public static final CascadeBot INS = new CascadeBot();
    public static final Logger LOGGER = LoggerFactory.getLogger(CascadeBot.class);

    private static Version version;
    private static Gson gson;

    private long startupTime;
    private ShardManager shardManager;
    private ArgumentManager argumentManager;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private PermissionsManager permissionsManager;
    private ModerationManager moderationManager;
    private OkHttpClient httpClient;
    private MusicHandler musicHandler;
    private EventWaiter eventWaiter;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(CascadeBot.class.getResourceAsStream("/version.txt"))) {
            version = Version.parseVer(scanner.next());
        }

        if (Environment.isProduction() || Arrays.stream(args).anyMatch("--json-logging"::equalsIgnoreCase)) {
            LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
            JsonLayout jsonLayout = new JsonLayout();
            jsonLayout.setAppendLineSeparator(true);
            jsonLayout.setTimestampFormat("yyyy-MM-dd HH:mm:ss.SSS");
            jsonLayout.setJsonFormatter(new JacksonJsonFormatter());
            encoder.setLayout(jsonLayout);
            ((ConsoleAppender<ILoggingEvent>) LogbackUtils.getRootLogger().getAppender("STDOUT")).setEncoder(encoder);
        }

        if (System.getenv("SENTRY_DSN") == null) {
            LOGGER.warn("You haven't set a Sentry DNS in the environment variables! Set SENTRY_DSN to your DSN for this to work!");
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
     * Clears all MDC keys that have the prefix "cascade."
     */
    public static void clearCascadeMDC() {
        for (String key : MDC.getCopyOfContextMap().keySet()) {
            if (key.startsWith("cascade.")) {
                MDC.remove(key);
            }
        }
    }

    /**
     * Runs once all shards are loaded
     */
    public void run() {
        LOGGER.info("All shards successfully logged in!");
        LOGGER.info("Cascade Bot version {} successfully booted up!", version);
        startupTime = System.currentTimeMillis();
    }


    private void init() {
        new Thread(new ConsoleReader()).start();

        GsonBuilder builder = new GsonBuilder();
        try {
            Config.init("config.yml");
        } catch (IOException e) {
            LOGGER.error("Error reading config file", e);
            ShutdownHandler.exitWithError();
            return;
        }

        // Sends a message to break up the status log flow to see what events apply to each bot run
        Config.INS.getEventWebhook().send(
                UnicodeConstants.ZERO_WIDTH_SPACE + "\n" +
                        StringUtils.repeat("-", 30) + " BOT RESTART " + StringUtils.repeat("-", 30) + "\n" +
                        UnicodeConstants.ZERO_WIDTH_SPACE);

        SentryClient client = Sentry.getStoredClient();
        client.setEnvironment(Environment.isDevelopment() ? "development" : "production");
        client.setRelease(version.toString());

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

        musicHandler = new MusicHandler(this);
        musicHandler.buildMusic();

        eventWaiter = new EventWaiter();
        gson = builder.create();

        try {
            DefaultShardManagerBuilder defaultShardManagerBuilder = new DefaultShardManagerBuilder()
                    .addEventListeners(new CommandListener())
                    .addEventListeners(new GeneralEventListener())
                    .addEventListeners(new ButtonEventListener())
                    .addEventListeners(new VoiceEventListener())
                    .addEventListeners(new JDAEventMetricsListener())
                    .addEventListeners(eventWaiter)
                    .setVoiceDispatchInterceptor(new JDAVoiceInterceptor(MusicHandler.getLavalink()))
                    .setToken(Config.INS.getBotToken())
                    .setShardsTotal(-1)
                    .setActivityProvider(shardId -> {
                        if (Environment.isDevelopment()) {
                            return Activity.streaming(" the devs mistakes", "https://twitch.tv/someone");
                        } else {
                            return Activity.playing("CascadeBot Version " + version);
                        }
                    })
                    .setBulkDeleteSplittingEnabled(false)
                    .setEnableShutdownHook(false);

            if (MusicHandler.isLavalinkEnabled()) {
                defaultShardManagerBuilder.addEventListeners(MusicHandler.getLavalink());
            } else {
                defaultShardManagerBuilder.setAudioSendFactory(new NativeAudioSendFactory());
            }

            shardManager = defaultShardManagerBuilder.build();
        } catch (LoginException e) {
            LOGGER.error("Error building JDA", e);
            ShutdownHandler.exitWithError();
            return;
        }

        argumentManager = new ArgumentManager();
        argumentManager.initArguments();
        commandManager = new CommandManager();
        permissionsManager = new PermissionsManager();
        permissionsManager.registerPermissions();
        moderationManager = new ModerationManager();

        Metrics.INS.cacheMetrics.addCache("guilds", GuildDataManager.getGuilds());

        Thread.setDefaultUncaughtExceptionHandler(((t, e) -> LOGGER.error("Uncaught exception in thread " + t, MDCException.from(e))));
        Thread.currentThread()
                .setUncaughtExceptionHandler(((t, e) -> LOGGER.error("Uncaught exception in thread " + t, MDCException.from(e))));

        RestAction.setDefaultFailure(throwable -> {
            if (throwable instanceof ErrorResponseException) {
                ErrorResponseException exception = (ErrorResponseException) throwable;
                Metrics.INS.failedRestActions.labels(exception.getErrorResponse().name()).inc();
            }
            LOGGER.error("Uncaught exception in rest action", MDCException.from(throwable));
        });

        setupTasks();

    }

    private void setupTasks() {
        new Task("prune-players") {
            @Override
            protected void execute() {
                musicHandler.purgeDisconnectedPlayers();
            }
        }.start(TimeUnit.MINUTES.toMillis(5), TimeUnit.MINUTES.toMillis(15));
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
        return LOGGER;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public ArgumentManager getArgumentManager() {
        return argumentManager;
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

    public ModerationManager getModerationManager() {
        return moderationManager;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public MusicHandler getMusicHandler() {
        return musicHandler;
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }

    public long getStartupTime() {
        return startupTime;
    }

    public long getUptime() {
        return System.currentTimeMillis() - startupTime;
    }

}
