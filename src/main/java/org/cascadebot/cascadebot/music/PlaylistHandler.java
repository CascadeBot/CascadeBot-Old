package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.managers.PlaylistManager;
import org.cascadebot.orchestra.data.Playlist;
import org.cascadebot.orchestra.data.TrackData;
import org.cascadebot.orchestra.data.enums.LoadPlaylistResult;
import org.cascadebot.orchestra.data.enums.NodeType;
import org.cascadebot.orchestra.data.enums.PlaylistType;
import org.cascadebot.orchestra.data.enums.SavePlaylistResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PlaylistHandler {

    public static void loadPlaylist(String name, TrackData trackData, long guildId, BiConsumer<LoadPlaylistResult, List<AudioTrack>> consumer) {
        Playlist guild = PlaylistManager.getPlaylistByName(guildId, PlaylistType.GUILD, name);
        Playlist user = PlaylistManager.getPlaylistByName(trackData.getUserId(), PlaylistType.USER, name);
        if (guild != null && user != null) {
            consumer.accept(LoadPlaylistResult.EXISTS_IN_ALL_SCOPES, null);
        } else if (guild != null) {
            loadLoadedPlaylist(guild, trackData, guildId, tracks -> {
                consumer.accept(LoadPlaylistResult.LOADED_GUILD, tracks);
            });
        } else if (user != null) {
            loadLoadedPlaylist(user, trackData, guildId, tracks -> {
                consumer.accept(LoadPlaylistResult.LOADED_USER, tracks);
            });
        } else {
            consumer.accept(LoadPlaylistResult.DOESNT_EXIST, null);
        }
    }

    public static void loadPlaylist(String name, TrackData trackData, PlaylistType scope, long guildId, BiConsumer<LoadPlaylistResult, List<AudioTrack>> consumer) {
        LoadPlaylistResult result = LoadPlaylistResult.DOESNT_EXIST;
        long owner = 0;
        switch (scope) {
            case GUILD:
                result = LoadPlaylistResult.LOADED_GUILD;
                owner = guildId;
                break;
            case USER:
                result = LoadPlaylistResult.LOADED_USER;
                owner = trackData.getUserId();
                break;
        }
        Playlist playlist = PlaylistManager.getPlaylistByName(owner, scope, name);
        if (playlist == null) {
            consumer.accept(LoadPlaylistResult.DOESNT_EXIST, null);
            return;
        }

        LoadPlaylistResult loadPlaylistResult = result;
        loadLoadedPlaylist(playlist, trackData, guildId, tracks -> {
            consumer.accept(loadPlaylistResult, tracks);
        });
    }

    public static void loadLoadedPlaylist(Playlist playlist, TrackData trackData, long guildId, Consumer<List<AudioTrack>> loadedConsumer) {
        List<AudioTrack> tracks = new ArrayList<>();
        for (String url : playlist.getTracks()) {
            CascadeBot.INS.getMusicHandler().getPlayer(String.valueOf(guildId), NodeType.GENERAL).loadLink(url, trackData, noMatch -> { // TODO not assume node type
                playlist.removeTrack(url);
            }, exception -> {
                playlist.removeTrack(url);
            }, loadedTracks -> {
                tracks.addAll(loadedTracks);
                if (tracks.size() == playlist.getTracks().size()) {
                    loadedConsumer.accept(tracks);
                }
            });
        }
    }

    public static SavePlaylistResult saveCurrentPlaylist(List<AudioTrack> tracks, long owner, PlaylistType scope, String name, boolean overwrite) {
        List<String> ids = new ArrayList<>();
        for (AudioTrack track : tracks) {
            ids.add(track.getIdentifier());
        }

        Playlist search = PlaylistManager.getPlaylistByName(owner, scope, name);
        if (search != null) {
            if (overwrite) {
                search.setTracks(ids);
                PlaylistManager.replacePlaylist(search);
                return SavePlaylistResult.OVERWRITE;
            } else {
                return SavePlaylistResult.ALREADY_EXISTS;
            }
        } else {
            PlaylistManager.savePlaylist(new Playlist(owner, name, scope, ids));
            return SavePlaylistResult.NEW;
        }
    }

}
