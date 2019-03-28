/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.cascadebot.cascadebot.data.managers.PlaylistManager;
import org.cascadebot.cascadebot.data.objects.Playlist;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.utils.StringsUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Consumer;

public class CascadePlayer {

    private Queue<AudioTrack> tracks = new LinkedList<>();

    private IPlayer player;

    protected boolean loop;

    public CascadePlayer(Long guildId) {
        player = MusicHandler.isLavalinkEnabled() ?
                MusicHandler.getLavaLink().getLink(guildId.toString()).getPlayer() :
                new LavaplayerPlayerWrapper(MusicHandler.createLavaLinkPlayer());
        player.addListener(new PlayerListener(this));
    }

    public IPlayer getPlayer() {
        return player;
    }

    public Queue<AudioTrack> getTracks() {
        return tracks;
    }

    public double getQueueLength() {
        double queueLength = player.getPlayingTrack().getDuration();
        for (AudioTrack track : tracks) {
            queueLength += track.getDuration();
        }
        return queueLength;
    }

    /**
     * Gets the progress bar for the current track
     *
     * @return The progress bar for the current track
     */
    public String getTrackProgressBar() {
        return StringsUtil.getProgressBar((100f / player.getPlayingTrack().getDuration() * player.getTrackPosition()));
    }

    public void addTrack(AudioTrack track) {
        if (player.getPlayingTrack() != null) {
            tracks.add(track);
        } else {
            player.playTrack(track);
        }
    }

    public void loop(boolean loop) {
        this.loop = loop;
    }

    public boolean skip() {
        try {
            player.playTrack(tracks.remove());
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void join(VoiceChannel channel) {
        MusicHandler.getLavaLink().getLink(channel.getGuild()).connect(channel);
    }

    public void leave(Guild guild) {
        guild.getAudioManager().closeAudioConnection();
    }

    public void stop() {
        tracks.clear();
        player.stopTrack();
    }

    public void loadLink(String stringUrl, Consumer<Void> noMatchConsumer, Consumer<FriendlyException> exceptionConsumer) {
        MusicHandler.playerManager.loadItem(stringUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                addTrack(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                for (AudioTrack track : audioPlaylist.getTracks()) {
                    addTrack(track);
                }
            }

            @Override
            public void noMatches() {
                noMatchConsumer.accept(null);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                exceptionConsumer.accept(e);
            }
        });
    }

    public void loadPlaylist(Playlist playlist) {
        for (String url : playlist.getTracks()) {
            loadLink(url, noMatch -> {
                playlist.removeTrack(url);
            }, exception -> {
                playlist.removeTrack(url);
            });
        }
    }

    public void saveCurrentPlaylist(long owner, PlaylistType scope, String name) {
        List<AudioTrack> tracks = new ArrayList<>();
        tracks.add(player.getPlayingTrack());
        tracks.addAll(this.tracks);

        List<String> ids = new ArrayList<>();
        for (AudioTrack track : tracks) {
            ids.add(track.getIdentifier());
        }

        Playlist search = PlaylistManager.getPlaylistByName(owner, scope, name);
        if (search != null) {
            search.setTracks(ids);
            PlaylistManager.replacePlaylist(search);
        } else {
            PlaylistManager.savePlaylist(new Playlist(owner, name, scope, ids));
        }
    }

}
