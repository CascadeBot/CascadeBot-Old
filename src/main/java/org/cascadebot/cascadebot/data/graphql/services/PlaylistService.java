package org.cascadebot.cascadebot.data.graphql.services;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLException;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.utils.Checks;
import org.cascadebot.cascadebot.data.graphql.objects.QLContext;
import org.cascadebot.cascadebot.data.managers.PlaylistManager;
import org.cascadebot.cascadebot.data.objects.Playlist;
import org.cascadebot.cascadebot.data.objects.PlaylistScope;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaylistService {

    @Getter
    private static PlaylistService instance = new PlaylistService();

    @GraphQLQuery
    public Playlist playlist(@GraphQLRootContext QLContext context, @GraphQLNonNull String id) {
        return PlaylistManager.getPlaylistById(id);
    }

    @GraphQLQuery
    @GraphQLNonNull
    public List<Playlist> allPlaylists(@GraphQLRootContext QLContext context, long ownerId, @GraphQLNonNull PlaylistScope scope) {
        return PlaylistManager.getPlaylists(ownerId, scope).into(new ArrayList<>());
    }

    @GraphQLQuery
    @GraphQLNonNull
    public String id(@GraphQLContext Playlist playlist) {
        return playlist.getPlaylistId().toHexString();
    }

    @GraphQLMutation
    public Playlist createPlaylist(@GraphQLRootContext QLContext context, @GraphQLNonNull String name, @GraphQLNonNull List<String> tracks, long ownerId, @GraphQLNonNull PlaylistScope scope) {
        Checks.notBlank(name, "name");
        Checks.notEmpty(tracks, "tracks");

        if (PlaylistManager.getPlaylistByName(ownerId, scope, name) != null) {
            throw new IllegalStateException("This playlist already exists!");
        }

        Playlist playlist = new Playlist(name, tracks, ownerId, scope);
        PlaylistManager.savePlaylist(playlist);
        return playlist;
    }

    @GraphQLMutation
    public String deletePlaylist(@GraphQLRootContext QLContext context, @GraphQLNonNull String id) {
        if (PlaylistManager.getPlaylistById(id) == null) {
            throw new IllegalStateException("There is no playlist by that ID to delete!");
        }

        PlaylistManager.deletePlaylistById(id);
        return id;
    }

    @GraphQLMutation(description = "Edits any fields on a playlist. Null fields are skipped.")
    public Playlist editPlaylist(@GraphQLRootContext QLContext context, @GraphQLNonNull String id, String name, List<String> tracks, Long ownerId, PlaylistScope playlistScope) {
        Playlist playlist = PlaylistManager.getPlaylistById(id);
        if (playlist == null) {
            throw new IllegalStateException("There is no playlist by that ID to edit! please create one instead!");
        }

        if (name != null) playlist.setName(name);
        if (tracks != null) playlist.setTracks(tracks);
        if (ownerId != null) playlist.setOwnerId(ownerId);
        if (playlistScope != null) playlist.setScope(playlistScope);

        PlaylistManager.replacePlaylist(playlist);

        return playlist;
    }



}
