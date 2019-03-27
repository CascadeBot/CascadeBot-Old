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
import lavalink.client.player.event.IPlayerEventListener;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.database.DebugLogCallback;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.Playlist;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.utils.StringsUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public class CascadePlayer {

    private Queue<AudioTrack> tracks = new LinkedList<>();

    private IPlayer player;

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

    public double getPlaylistLength() {
        double playlistLength = player.getPlayingTrack().getDuration();
        for (AudioTrack track : tracks) {
            playlistLength += track.getDuration();
        }
        return playlistLength;
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
        if (player.getPlayingTrack() != null) {
            tracks.add(track);
        } else {
            player.playTrack(track);
        }
    }

    public boolean skip() {
        try {
            player.playTrack(tracks.remove());
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
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

    public List<Playlist> getPlaylists(long owner, PlaylistType scope) {
        List<Playlist> playlists = new ArrayList<>();
        for (Playlist playlist : CascadeBot.INS.getDatabaseManager().getDatabase().getCollection("playlists", Playlist.class).find(combine(eq("ownerID", owner), eq("scope", scope)))) {
            playlists.add(playlist);
        }
        return playlists;
    }

    public void loadPlaylist(Playlist playlist) {
        for (String url : playlist.getTracks()) {
            loadLink(url, avoid -> {
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
        boolean exists = false;
        List<String> ids = new ArrayList<>();
        for (AudioTrack track : tracks) {
            ids.add(track.getIdentifier());
        }
        for(Playlist playlist : getPlaylists(owner, scope)) {
            if(playlist.getName().equals(name)) {
                exists = true;
                playlist.setTracks(ids);
                CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
                    database.getCollection("playlists", Playlist.class).replaceOne(eq("_id", playlist.getPlaylistID()), playlist, new DebugLogCallback<>("Replaced Playlist ID " + playlist.getPlaylistID().toString()));
                });
                break;
            }
        }
        if(!exists) {
            Playlist playlist = new Playlist(owner, name, scope, ids);
            CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
                database.getCollection("playlists", Playlist.class).insertOne(playlist, new DebugLogCallback<>("Inserted new playlist with ID " + playlist.getPlaylistID().toString()));
            });
        }
    }
}
