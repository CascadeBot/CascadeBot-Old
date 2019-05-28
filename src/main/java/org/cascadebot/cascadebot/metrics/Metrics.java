/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import io.prometheus.client.cache.caffeine.CacheMetricsCollector;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;

import java.io.IOException;

public class Metrics {

    public Gauge guildCount = Gauge.build().name("guild_count").help("The number of guilds Cascade is in").register();
    public Counter commandsExecuted = Counter.build()
                                         .name("commands_executed")
                                         .help("The number of commands executed this session")
                                         .labelNames("class")
                                         .register();
    public Counter commandsSubmitted = Counter.build()
                                             .name("commands_submitted")
                                             .help("The number of commands submitted this session")
                                             .labelNames("class")
                                             .register();
    public Summary commandExecutionTime = Summary.build()
                                                 .name("command_execution_time")
                                                 .help("The average execution time of commands")
                                                 .labelNames("class")
                                                 .register();
    public CacheMetricsCollector cacheMetrics = new CacheMetricsCollector().register();

    private HTTPServer prometheusServer;

    public Metrics() {
        try {
            prometheusServer = new HTTPServer(Config.INS.getPrometheusPort());
        } catch (IOException e) {
            CascadeBot.LOGGER.error("Error starting the prometheus server!", e);
        }

        DefaultExports.initialize();

    }

    public HTTPServer getPrometheusServer() {
        return prometheusServer;
    }

}
