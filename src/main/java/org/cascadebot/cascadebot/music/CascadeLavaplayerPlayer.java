/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 *  Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.LavaplayerPlayerWrapper;
import lavalink.client.player.event.IPlayerEventListener;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class CascadeLavaplayerPlayer extends CascadePlayer {

    private final AudioPlayer player;

    public CascadeLavaplayerPlayer(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    @Override
    public void playTrack(AudioTrack track) {
        player.playTrack(track);
    }

    @Override
    public void stopTrack() {
        player.stopTrack();
    }

    @Override
    public boolean isPaused() {
        return player.isPaused();
    }

    @Override
    public void setPaused(boolean b) {
        player.setPaused(b);
    }

    @Override
    public long getTrackPosition() {
        if (player.getPlayingTrack() == null) throw new IllegalStateException("Not playing anything");

        return player.getPlayingTrack().getPosition();
    }

    @Override
    public void seekTo(long position) {
        if (player.getPlayingTrack() == null) throw new IllegalStateException("Not playing anything");

        player.getPlayingTrack().setPosition(position);
    }

    @Override
    public int getVolume() {
        return player.getVolume();
    }

    @Override
    public void setVolume(int volume) {
        player.setVolume(volume);
    }

    @Override
    public void addListener(IPlayerEventListener listener) {
        player.addListener((AudioEventListener) listener);
    }

    @Override
    public void removeListener(IPlayerEventListener listener) {
        player.removeListener((AudioEventListener) listener);
    }

    @Override
    public void join(VoiceChannel channel) {
        channel.getGuild().getAudioManager().openAudioConnection(channel);
    }

    @Override
    public void leave() {
        getGuild().getAudioManager().closeAudioConnection();
    }

    @Override
    public VoiceChannel getConnectedChannel() {
        return getGuild().getAudioManager().getConnectedChannel();
    }

}
