/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.cascadebot.cascadebot.Cascade;
import org.cascadebot.cascadebot.commands.music.SkipCommand;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.utils.votes.VoteButtonGroup;

public class VoiceEventListener extends ListenerAdapter {

    @Override
    public void onGenericGuildVoice(GenericGuildVoiceEvent event) {
        // TODO: handle leaving & moving of players so we don't get lonely :(
        if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) return;

        VoiceChannel botCurrentChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
        VoteButtonGroup voteButtonGroup = SkipCommand.voteMap.get(event.getGuild().getIdLong());
        long userId = event.getMember().getUser().getIdLong();

        if (event instanceof GuildVoiceJoinEvent) {
            GuildVoiceJoinEvent joinEvent = (GuildVoiceJoinEvent) event;
            if (joinEvent.getChannelJoined().equals(botCurrentChannel) && voteButtonGroup != null) {
                if (Cascade.INS.getPermissionsManager().isAuthorised(Cascade.INS.getCommandManager().getCommandByDefault("skip"), GuildDataManager.getGuildData(joinEvent.getChannelJoined().getGuild().getIdLong()), joinEvent.getMember())) {
                    voteButtonGroup.allowUser(userId);
                }
            }
        } else if (event instanceof GuildVoiceMoveEvent) {
            GuildVoiceMoveEvent moveEvent = (GuildVoiceMoveEvent) event;
            if (moveEvent.getChannelJoined().equals(botCurrentChannel) && voteButtonGroup != null) {
                if (Cascade.INS.getPermissionsManager().isAuthorised(Cascade.INS.getCommandManager().getCommandByDefault("skip"), GuildDataManager.getGuildData(moveEvent.getChannelJoined().getGuild().getIdLong()), moveEvent.getMember())) {
                    voteButtonGroup.allowUser(userId);
                }
            } else if (moveEvent.getChannelLeft().equals(botCurrentChannel) && voteButtonGroup != null) {
                voteButtonGroup.denyUser(userId);
            }
        } else if (event instanceof GuildVoiceLeaveEvent) {
            GuildVoiceLeaveEvent leaveEvent = (GuildVoiceLeaveEvent) event;
            if (leaveEvent.getChannelLeft().equals(botCurrentChannel) && voteButtonGroup != null) {
                voteButtonGroup.denyUser(userId);
            }
        }
    }

}
