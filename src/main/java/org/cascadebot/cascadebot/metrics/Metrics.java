/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.SimpleCollector;
import io.prometheus.client.Summary;
import io.prometheus.client.cache.caffeine.CacheMetricsCollector;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;

import java.io.IOException;

public class Metrics {

    public static final Metrics INS = new Metrics();

    public Counter commandsExecuted = Counter.build()
            .name("cascade_commands_executed_total")
            .help("The number of commands executed this session")
            .labelNames("command")
            .register();
    public Counter commandsSubmitted = Counter.build()
            .name("cascade_commands_submitted_total")
            .help("The number of commands submitted this session")
            .labelNames("command")
            .register();
    public Summary commandExecutionTime = Summary.build()
            .name("cascade_command_execution_time_seconds")
            .help("The average execution time of commands")
            .labelNames("command")
            .register();
    public Counter commandsErrored = Counter.build()
            .name("cascade_commands_errored_total")
            .help("The number of commands that have errored out in this session")
            .labelNames("command")
            .register();
    public Counter buttonsPressed = Counter.build()
            .name("cascade_buttons_pressed_total")
            .help("The number of buttons pressed in this session")
            .labelNames("button", "type")
            .register();
    public Counter messagesSent = Counter.build()
            .name("cascade_messages_sent_total")
            .help("The numbers of messages sent in this session")
            .labelNames("type")
            .register();

    public Counter tracksPlayed = Counter.build()
            .name("cascade_tracks_played_total")
            .help("The number of tracks that have been played in this session")
            .register();


    public Counter failedRestActions = Counter.build()
            .name("cascade_failed_rest_actions_total")
            .help("The number of uncaught failed rest actions in this session")
            .labelNames("error_code")
            .register();
    public Counter jdaEvents = Counter.build()
            .name("cascade_jda_events_total")
            .help("The number of JDA events executed in this session")
            .labelNames("event")
            .register();


    public CacheMetricsCollector cacheMetrics = new CacheMetricsCollector().register();

    private BotMetricsCollector botMetricsCollector;
    private HTTPServer prometheusServer;

    private Metrics() {
        try {
            prometheusServer = new HTTPServer(Config.INS.getPrometheusPort());
        } catch (IOException e) {
            CascadeBot.LOGGER.error("Error starting the prometheus server!", e);
        }

        DefaultExports.initialize();
        botMetricsCollector = new BotMetricsCollector();
        botMetricsCollector.register();
    }

    public HTTPServer getPrometheusServer() {
        return prometheusServer;
    }

}
