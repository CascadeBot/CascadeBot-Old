/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.cascadebot.cascadebot.commands.music.SkipCommand;

public class VoiceEventListener extends ListenerAdapter {

    @Override
    public void onGenericGuildVoice(GenericGuildVoiceEvent event) {
        // TODO: handle leaving & moving of players so we don't get lonely :(
        if (event instanceof GuildVoiceJoinEvent) {
            GuildVoiceJoinEvent joinEvent = (GuildVoiceJoinEvent) event;
            if (joinEvent.getChannelJoined().equals(event.getGuild().getSelfMember().getVoiceState().getChannel())) {
                SkipCommand.voteMap.get(joinEvent.getGuild().getIdLong()).getAllowedUsers().remove(event.getMember().getUser().getIdLong());
            }
        } else if (event instanceof GuildVoiceMoveEvent) {
            GuildVoiceMoveEvent moveEvent = (GuildVoiceMoveEvent) event;
            if (moveEvent.getChannelJoined().equals(event.getGuild().getSelfMember().getVoiceState().getChannel())) {
                SkipCommand.voteMap.get(moveEvent.getGuild().getIdLong()).getAllowedUsers().remove(event.getMember().getUser().getIdLong());
            }
            if (moveEvent.getChannelLeft().equals(event.getGuild().getSelfMember().getVoiceState().getChannel())) {
                SkipCommand.voteMap.get(moveEvent.getGuild().getIdLong()).getAllowedUsers().add(event.getMember().getUser().getIdLong());
            }
        } else if (event instanceof GuildVoiceLeaveEvent) {
            GuildVoiceLeaveEvent leaveEvent = (GuildVoiceLeaveEvent) event;
            if (leaveEvent.getChannelLeft().equals(event.getGuild().getSelfMember().getVoiceState().getChannel())) {
                SkipCommand.voteMap.get(leaveEvent.getGuild().getIdLong()).getAllowedUsers().add(event.getMember().getUser().getIdLong());
            }
        }
    }

}
