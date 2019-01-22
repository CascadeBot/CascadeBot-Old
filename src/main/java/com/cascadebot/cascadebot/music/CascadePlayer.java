/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.music;

import com.cascadebot.cascadebot.utils.StringsUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;

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

    public double getPlaylistLength() {
        double start = player.getPlayingTrack().getDuration();
        for(AudioTrack track : tracks) {
            start += track.getDuration();
        }
        return start;
    }

    /**
     * Gets the progress bar for the current track
     *
     * @return The progress bar for the current track
     */
    public String getTrackProgressBar() {
        return StringsUtil.getProgressBar((100f / player.getPlayingTrack().getDuration() * player.getTrackPosition()));
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
