/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.CascadeBot;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Events extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        ShardManager shardManager = CascadeBot.instance().getShardManager();
        if (shardManager.getShards().size() == shardManager.getShardsTotal()) {
            CascadeBot.instance().run();
        }
    }

}
