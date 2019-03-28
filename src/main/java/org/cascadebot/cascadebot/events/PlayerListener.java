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

public class PlayerListener implements IPlayerEventListener {

    private CascadePlayer player;

    public PlayerListener(CascadePlayer player) {
        this.player = player;
    }

    @Override
    public void onEvent(PlayerEvent playerEvent) {
        if (playerEvent instanceof TrackEndEvent) {
            try {
                if(player.getLoopMode().equals(CascadePlayer.LoopMode.DISABLED) || player.getLoopMode().equals(CascadePlayer.LoopMode.PLAYLIST)) {
                    if (player.getLoopMode().equals(CascadePlayer.LoopMode.PLAYLIST)) {
                        TrackEndEvent endEvent = (TrackEndEvent) playerEvent;
                        player.getTracks().add(endEvent.getTrack());
                    }
                    AudioTrack audioTrack = player.getTracks().remove();
                    player.getPlayer().playTrack(audioTrack);
                } else if (player.getLoopMode().equals(CascadePlayer.LoopMode.SONG)) {
                    TrackEndEvent endEvent = (TrackEndEvent) playerEvent;
                    player.getPlayer().playTrack(endEvent.getTrack());
                }
            } catch (Exception e) {
                //TODO Add Events for playlist complete
            }
        }
    }

}
