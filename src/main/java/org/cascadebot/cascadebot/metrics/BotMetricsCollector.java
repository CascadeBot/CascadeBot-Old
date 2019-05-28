/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import lavalink.client.io.Link;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.music.MusicHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BotMetricsCollector extends Collector {

    @Override
    public List<MetricFamilySamples> collect() {

        List<MetricFamilySamples> metricFamilySamples = new ArrayList<>();

        ShardManager shardManager = CascadeBot.INS.getShardManager();
        if (shardManager.getShards().size() == shardManager.getShardsTotal()) {
            GaugeMetricFamily entities = new GaugeMetricFamily("cascade_jda_entities", "The number of JDA entities registered", List.of("shard", "entity_type"));
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

            if (MusicHandler.isLavalinkEnabled()) {
                GaugeMetricFamily lavalinkInfo = new GaugeMetricFamily("cascade_lavalink_info", "Information about Lavalink", List.of("info"));
                lavalinkInfo.addMetric(List.of("links"), MusicHandler.getLavalink().getLinks().size());
                lavalinkInfo.addMetric(List.of("connected_voice_channels"), MusicHandler.getLavalink().getLinks().stream().filter(link -> link.getState() == Link.State.CONNECTED).count());
            } else {
                
            }

        }
        return null;
    }

}
