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
                AudioTrack audioTrack = player.getTracks().remove();
                player.getPlayer().playTrack(audioTrack);
            } catch (Exception e) {
                //NOTHING!
            }
        }
    }

}
