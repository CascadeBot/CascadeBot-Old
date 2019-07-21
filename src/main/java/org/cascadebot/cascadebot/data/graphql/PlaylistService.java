package org.cascadebot.cascadebot.data.graphql;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.data.managers.PlaylistManager;
import org.cascadebot.cascadebot.data.objects.Playlist;
import org.cascadebot.cascadebot.data.objects.PlaylistScope;
import spark.Request;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaylistService {

    @Getter
    private static PlaylistService instance = new PlaylistService();

    @GraphQLQuery
    public Playlist playlist(@GraphQLRootContext Request request, @GraphQLNonNull String id) {
        return PlaylistManager.getPlaylistById(id);
    }

    @GraphQLQuery
    @GraphQLNonNull
    public List<Playlist> allPlaylists(@GraphQLRootContext Request request, long ownerId, @GraphQLNonNull PlaylistScope scope) {
        return PlaylistManager.getPlaylists(ownerId, scope).into(new ArrayList<>());
    }

    @GraphQLQuery
    @GraphQLNonNull
    public String id(@GraphQLContext Playlist playlist) {
        return playlist.getPlaylistId().toHexString();
    }

}
