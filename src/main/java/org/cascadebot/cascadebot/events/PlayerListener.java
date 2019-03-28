/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.event.IPlayerEventListener;
import lavalink.client.player.event.PlayerEvent;
import lavalink.client.player.event.TrackEndEvent;
import org.cascadebot.cascadebot.music.CascadePlayer;

import java.util.NoSuchElementException;

public class PlayerListener implements IPlayerEventListener {

    private CascadePlayer player;

    public PlayerListener(CascadePlayer player) {
        this.player = player;
    }

    private int songPlayCount = 0;

    @Override
    public void onEvent(PlayerEvent playerEvent) {
        if (playerEvent instanceof TrackEndEvent) {
            TrackEndEvent endEvent = (TrackEndEvent) playerEvent;
            try {
                if (player.getLoopMode().equals(CascadePlayer.LoopMode.DISABLED) || player.getLoopMode().equals(CascadePlayer.LoopMode.PLAYLIST)) {
                    if (player.getLoopMode().equals(CascadePlayer.LoopMode.PLAYLIST)) {
                        // Add the track to the end of the queue to be repeated
                        player.getTracks().add(endEvent.getTrack());
                        if (player.isShuffleEnabled()) {
                            if (++songPlayCount % player.getTracks().size() == 0) {
                                player.shuffle(); //Shuffle when the tracks start over.
                            }
                        }
                    }
                    // Take the next track in the queue, remove it from the queue and play it
                    AudioTrack audioTrack = player.getTracks().remove();
                    player.getPlayer().playTrack(audioTrack);
                } else if (player.getLoopMode().equals(CascadePlayer.LoopMode.SONG)) {
                    // Take the song that just finished and repeat it
                    player.getPlayer().playTrack(endEvent.getTrack());
                }
            } catch (NoSuchElementException e) {
                // No more songs left in the queue
                songPlayCount = 0;
                // TODO: Anything more to this?
            }
        }
    }

}
