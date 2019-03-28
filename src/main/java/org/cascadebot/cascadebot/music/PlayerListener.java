/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.event.IPlayerEventListener;
import lavalink.client.player.event.PlayerEvent;
import lavalink.client.player.event.TrackEndEvent;

public class PlayerListener implements IPlayerEventListener {

    CascadePlayer player;

    public PlayerListener(CascadePlayer player) {
        this.player = player;
    }

    @Override
    public void onEvent(PlayerEvent playerEvent) {
        if (playerEvent instanceof TrackEndEvent) {
            try {
                if(player.loop.equals(CascadePlayer.LoopType.DISABLED) || player.loop.equals(CascadePlayer.LoopType.PLAYLIST)) {
                    if (player.loop.equals(CascadePlayer.LoopType.PLAYLIST)) {
                        TrackEndEvent endEvent = (TrackEndEvent) playerEvent;
                        player.getTracks().add(endEvent.getTrack());
                    }
                    AudioTrack audioTrack = player.getTracks().remove();
                    player.getPlayer().playTrack(audioTrack);
                } else if (player.loop.equals(CascadePlayer.LoopType.SONG)) {
                    TrackEndEvent endEvent = (TrackEndEvent) playerEvent;
                    player.getPlayer().playTrack(endEvent.getTrack());
                }
            } catch (Exception e) {
                //TODO Add Events for playlist complete
            }
        }
    }

}
