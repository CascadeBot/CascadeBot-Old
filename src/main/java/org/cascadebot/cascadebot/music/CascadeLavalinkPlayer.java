/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 *  Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.io.LavalinkSocket;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.IPlayerEventListener;
import lombok.Getter;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CascadeLavalinkPlayer implements CascadePlayer {

    @Getter
    private Map<Integer, Float> currentBands = new HashMap<>();

    private LavalinkPlayer lavalinkPlayer;

    public CascadeLavalinkPlayer(LavalinkPlayer lavalinkPlayer) {
        for (int i = 0; i < 15; i++) {
            currentBands.put(i, 0.0f);
        }
        this.lavalinkPlayer = lavalinkPlayer;
    }

    @Override
    public void join(VoiceChannel channel) {
        getLink().connect(channel);
    }

    @Override
    public void leave() {
        queue.clear();
        stopTrack(); // Clear queue and stop track to prevent errors with lavalink server
        getLink().disconnect();
    }

    @Override
    public VoiceChannel getConnectedChannel() {
        if (getLink().getChannel() != null) {
            return CascadeBot.INS.getShardManager().getVoiceChannelById(getLink().getChannel());
        } else {
            return null;
        }
    }

    public JdaLink getLink() {
        return CascadeBot.INS.getMusicHandler().getLavaLink().getLink(getGuild());
    }

    public void setBands(Map<Integer, Float> bands) throws UnsupportedOperationException {
        LavalinkSocket node =  lavalinkPlayer.getLink().getNode(false);
        if (node != null) {
            JSONObject json = new JSONObject();
            json.put("op", "equalizer");
            json.put("guildId", lavalinkPlayer.getLink().getGuildId());
            JSONArray jsonArray = new JSONArray();
            for (Map.Entry<Integer, Float> entry : bands.entrySet()) {
                if (entry.getKey() < 0 || entry.getKey() > Equalizer.BAND_COUNT - 1) { // Make sure band is in range
                    throw new UnsupportedOperationException("Cannot set a band that doesn't exist");
                }
                JSONObject bandJson = new JSONObject();
                bandJson.put("band", entry.getKey());
                bandJson.put("gain", entry.getValue());
                jsonArray.put(bandJson);
            }
            json.put("bands", jsonArray);
            node.send(json.toString());
            // This is happening after all the bands are ran through to make sure the bands are actually set before changing them client side
            for (Map.Entry<Integer, Float> entry : bands.entrySet()) {
                currentBands.replace(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setBand(int band, float gain) {
        Map<Integer, Float> bands = new HashMap<>();
        bands.put(band, gain);
        setBands(bands);
    }

    //region Player methods
    @Override
    public AudioTrack getPlayingTrack() {
        return lavalinkPlayer.getPlayingTrack();
    }

    @Override
    public void playTrack(AudioTrack audioTrack) {
        lavalinkPlayer.playTrack(audioTrack);
    }

    @Override
    public void stopTrack() {
        lavalinkPlayer.stopTrack();
    }

    @Override
    public void setPaused(boolean b) {
        lavalinkPlayer.setPaused(b);
    }

    @Override
    public boolean isPaused() {
        return lavalinkPlayer.isPaused();
    }

    @Override
    public long getTrackPosition() {
        return lavalinkPlayer.getTrackPosition();
    }

    @Override
    public void seekTo(long position) {
        lavalinkPlayer.seekTo(position);
    }

    @Override
    public void setVolume(int volumeLevel) {
        lavalinkPlayer.setVolume(volumeLevel);
    }

    @Override
    public int getVolume() {
        return lavalinkPlayer.getVolume();
    }

    @Override
    public void addListener(IPlayerEventListener iPlayerEventListener) {
        lavalinkPlayer.addListener(iPlayerEventListener);
    }

    @Override
    public void removeListener(IPlayerEventListener iPlayerEventListener) {
        lavalinkPlayer.removeListener(iPlayerEventListener);
    }
    //endregion

}
