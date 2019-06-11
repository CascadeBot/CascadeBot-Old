/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade;

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
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.requests.RestAction;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascade.commandmeta.CommandManager;
import org.cascadebot.cascade.data.Config;
import org.cascadebot.cascade.data.database.DatabaseManager;
import org.cascadebot.cascade.data.managers.GuildDataManager;
import org.cascadebot.cascade.events.ButtonEventListener;
import org.cascadebot.cascade.events.CommandListener;
import org.cascadebot.cascade.events.GeneralEventListener;
import org.cascadebot.cascade.events.JDAEventMetricsListener;
import org.cascadebot.cascade.events.VoiceEventListener;
import org.cascadebot.cascade.metrics.Metrics;
import org.cascadebot.cascade.moderation.ModerationManager;
import org.cascadebot.cascade.music.MusicHandler;
import org.cascadebot.cascade.permissions.PermissionsManager;
import org.cascadebot.cascade.tasks.Task;
import org.cascadebot.cascade.utils.EventWaiter;
import org.cascadebot.cascade.utils.LogbackUtils;
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

public class Cascade {

    public static final Cascade INS = new Cascade();
    public static final Logger LOGGER = LoggerFactory.getLogger(Cascade.class);

    private static Version version;
    private static Gson gson;

    private long startupTime;
    private ShardManager shardManager;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private PermissionsManager permissionsManager;
    private ModerationManager moderationManager;
    private OkHttpClient httpClient;
    private MusicHandler musicHandler;
    private EventWaiter eventWaiter;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(Cascade.class.getResourceAsStream("/version.txt"))) {
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
                Cascade.INS.getSelfUser().getId(), Permission.ALL_GUILD_PERMISSIONS);
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
                    .setToken(Config.INS.getBotToken())
                    .setShardsTotal(-1)
                    .setGameProvider(shardId -> {
                        if (Environment.isDevelopment()) {
                            return Game.streaming(" the devs mistakes", "https://twitch.tv/someone");
                        } else {
                            return Game.playing("CascadeBot Version " + version);
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

        commandManager = new CommandManager();
        permissionsManager = new PermissionsManager();
        permissionsManager.registerPermissions();
        moderationManager = new ModerationManager();

        Metrics.INS.cacheMetrics.addCache("guild", GuildDataManager.getGuilds());

        Thread.setDefaultUncaughtExceptionHandler(((t, e) -> LOGGER.error("Uncaught exception in thread " + t, MDCException.from(e))));
        Thread.currentThread()
                .setUncaughtExceptionHandler(((t, e) -> LOGGER.error("Uncaught exception in thread " + t, MDCException.from(e))));

        RestAction.DEFAULT_FAILURE = throwable -> {
            if (throwable instanceof ErrorResponseException) {
                ErrorResponseException exception = (ErrorResponseException) throwable;
                Metrics.INS.failedRestActions.labels(exception.getErrorResponse().name()).inc();
            }
            LOGGER.error("Uncaught exception in rest action", MDCException.from(throwable));
        };

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
