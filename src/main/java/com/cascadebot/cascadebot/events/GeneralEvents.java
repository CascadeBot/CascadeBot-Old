/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.data.Config;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;

public class GeneralEvents extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        ShardManager shardManager = CascadeBot.INS.getShardManager();
        if (shardManager.getShards().size() == shardManager.getShardsTotal()) {
            CascadeBot.INS.run();
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        if (!StringUtils.isBlank(Config.INS.getGuildWelcomeMessage())) {
            Guild guild = event.getGuild();
            guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(Config.INS.getGuildWelcomeMessage()).queue();
            }, error -> { /* Do nothing */ });
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        if (!StringUtils.isBlank(Config.INS.getGuildGoodbyeMessage())) {
            event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(Config.INS.getGuildGoodbyeMessage()).queue();
            }, error -> { /* Do nothing */ });
        }
    }

}
