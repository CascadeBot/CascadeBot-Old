/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.event.IPlayerEventListener;
import lavalink.client.player.event.PlayerEvent;
import lavalink.client.player.event.TrackEndEvent;
import org.cascadebot.cascadebot.metrics.Metrics;
import org.cascadebot.cascadebot.music.CascadePlayer;

import java.util.NoSuchElementException;

public class PlayerListener implements IPlayerEventListener, AudioEventListener {

    private CascadePlayer player;

    private int songPlayCount = 0;

    public PlayerListener(CascadePlayer player) {
        this.player = player;
    }

    @Override
    public void onEvent(PlayerEvent playerEvent) {
        if (playerEvent instanceof TrackEndEvent) {
            onEnd(((TrackEndEvent) playerEvent).getTrack());
        }
    }

    @Override
    public void onEvent(AudioEvent audioEvent) {
        if (audioEvent instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent) {
            onEnd(((com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent) audioEvent).track);
        }
    }

    private void onEnd(AudioTrack track) {
        songPlayCount++;
        Metrics.INS.tracksPlayed.inc();
        try {
            if (player.getLoopMode().equals(CascadePlayer.LoopMode.DISABLED) || player.getLoopMode().equals(CascadePlayer.LoopMode.PLAYLIST)) {
                if (player.getLoopMode().equals(CascadePlayer.LoopMode.PLAYLIST)) {
                    // Add the track to the end of the queue to be repeated
                    player.getQueue().add(track.makeClone());
                    if (player.isShuffleEnabled()) {
                        if (songPlayCount % player.getQueue().size() == 0) {
                            player.shuffle(); //Shuffle when the tracks start over.
                        }
                    }
                }
                // Take the next track in the queue, remove it from the queue and play it
                AudioTrack audioTrack = player.getQueue().remove();
                player.playTrack(audioTrack);
            } else if (player.getLoopMode().equals(CascadePlayer.LoopMode.SONG)) {
                // Take the song that just finished and repeat it
                player.playTrack(track.makeClone());
            }
        } catch (NoSuchElementException e) {
            // No more songs left in the queue
            songPlayCount = 0;
            // TODO: Anything more to this?
        }
    }

}
