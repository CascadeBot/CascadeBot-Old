package org.cascadebot.cascadebot.data.graphql.services;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

}
