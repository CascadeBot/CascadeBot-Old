/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CascadePlayer {

    private Queue<AudioTrack> tracks = new LinkedList<>();

    private static Map<Long, CascadePlayer> playerMap = new HashMap<>();

    private IPlayer player;

    private CascadePlayer(Long guildId) {
        player = MusicHandler.isLavalinkEnabled() ?
                MusicHandler.getLavaLink().getLink(guildId.toString()).getPlayer() :
                new LavaplayerPlayerWrapper(MusicHandler.createLavaLinkPlayer());
        player.addListener(new PlayerListener(this));
    }

    public static CascadePlayer getCascadePlayer(Long guildId) {
        return playerMap.computeIfAbsent(guildId, CascadePlayer::new);
    }

    public IPlayer getPlayer() {
        return player;
    }

    public Queue<AudioTrack> getTracks() {
        return tracks;
    }

    public String getProgressBar() {
        float percentage = (100f / player.getPlayingTrack().getDuration() * player.getTrackPosition());
        return "[" + StringUtils.repeat("▬", (int) Math.round((double) percentage / 10)) +
                "](https://github.com/CascadeBot)" +
                StringUtils.repeat("▬", 10 - (int) Math.round((double) percentage / 10)) +
                " " + Math.round(percentage * 100.0) / 100.0 + "%";
    }

    public double getPlaylistLength() {
        double start = player.getPlayingTrack().getDuration();
        for(AudioTrack track : tracks) {
            start += track.getDuration();
        }
        return start;
    }

    //TODO implement player methods
    public void addTrack(AudioTrack track) {
        if(player.getPlayingTrack() != null) {
            tracks.add(track);
        } else {
            player.playTrack(track);
        }
    }
}
