/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.utils.FormatUtils;

public class GeneralEventListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        ShardManager shardManager = CascadeBot.INS.getShardManager();
        if (shardManager.getShards().size() == shardManager.getShardsTotal()) {
            CascadeBot.INS.run();
            Config.INS.getEventWebhook().send(
                    MessageType.SUCCESS.getEmoji() + " All shards ready!"
            );
        }
    }

    @Override
    public void onStatusChange(StatusChangeEvent event) {
        switch (event.getNewStatus()) {
            case CONNECTED:
            case DISCONNECTED:
            case RECONNECT_QUEUED:
            case ATTEMPTING_TO_RECONNECT:
            case SHUTTING_DOWN:
            case SHUTDOWN:
            case FAILED_TO_LOGIN:
                Config.INS.getEventWebhook().send(String.format(
                        UnicodeConstants.ROBOT + " Status Update: `%s` to `%s` on shard: `%d`",
                        FormatUtils.formatEnum(event.getOldStatus()),
                        FormatUtils.formatEnum(event.getNewStatus()),
                        event.getJDA().getShardInfo().getShardId()
                ));
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
        CascadeBot.INS.getMusicHandler().removePlayer(event.getGuild().getIdLong());

        if (!StringUtils.isBlank(Config.INS.getGuildGoodbyeMessage())) {
            event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(Config.INS.getGuildGoodbyeMessage()).queue();
            }, error -> { /* Do nothing */ });
        }
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        for (Group group : GuildDataManager.getGuildData(event.getGuild().getIdLong()).getPermissions().getGroups()) {
            group.unlinkRole(event.getRole().getIdLong());
        }
    }

}
