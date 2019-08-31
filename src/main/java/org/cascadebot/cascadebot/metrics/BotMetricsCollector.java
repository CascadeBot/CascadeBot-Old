/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.cascadebot.cascadebot.CascadeBot;

import java.util.ArrayList;
import java.util.List;

public class BotMetricsCollector extends Collector {

    @Override
    public List<MetricFamilySamples> collect() {

        List<MetricFamilySamples> metricFamilySamples = new ArrayList<>();

        ShardManager shardManager = CascadeBot.INS.getShardManager();
        if (shardManager != null) {
            if (shardManager.getShards().size() == shardManager.getShardsTotal()) {
                GaugeMetricFamily entities = new GaugeMetricFamily("cascade_jda_entities", "The number of JDA entities registered", List
                        .of("shard", "entity_type"));
                for (JDA shard : shardManager.getShards()) {
                    String shardId = String.valueOf(shard.getShardInfo().getShardId());
                    entities.addMetric(List.of(shardId, "guilds"), shard.getGuildCache().size());
                    entities.addMetric(List.of(shardId, "users"), shard.getUserCache().size());
                    entities.addMetric(List.of(shardId, "text_channels"), shard.getTextChannelCache().size());
                    entities.addMetric(List.of(shardId, "voice_channels"), shard.getVoiceChannelCache().size());
                    entities.addMetric(List.of(shardId, "categories"), shard.getCategoryCache().size());
                }
                entities.addMetric(List.of("all", "guilds"), shardManager.getGuildCache().size());
                entities.addMetric(List.of("all", "users"), shardManager.getUserCache().size());
                entities.addMetric(List.of("all", "text_channels"), shardManager.getTextChannelCache().size());
                entities.addMetric(List.of("all", "voice_channels"), shardManager.getVoiceChannelCache().size());
                entities.addMetric(List.of("all", "categories"), shardManager.getCategoryCache().size());
                metricFamilySamples.add(entities);
            }

            GaugeMetricFamily uptime = new GaugeMetricFamily("cascade_uptime", "", List.of());
            uptime.addMetric(List.of(), CascadeBot.INS.getUptime());
            metricFamilySamples.add(uptime);
        }

        return metricFamilySamples;
    }

}
