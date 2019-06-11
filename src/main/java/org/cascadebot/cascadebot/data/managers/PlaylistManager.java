/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.managers;

import com.mongodb.client.MongoIterable;
import org.cascadebot.cascadebot.Cascade;
import org.cascadebot.cascadebot.data.database.DebugLogCallback;
import org.cascadebot.cascadebot.data.objects.Playlist;
import org.cascadebot.cascadebot.data.objects.PlaylistType;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public final class PlaylistManager {

    private static final String COLLECTION = "playlists";

    public static MongoIterable<Playlist> getPlaylists(long ownerID, PlaylistType scope) {
        return Cascade.INS.getDatabaseManager().getDatabase().getCollection(COLLECTION, Playlist.class)
                .find(
                        combine(
                                eq("ownerID", ownerID),
                                eq("scope", scope)
                        )
                );
    }

    public static Playlist getPlaylistByName(long ownerID, PlaylistType scope, String name) {
        for (Playlist playlist : getPlaylists(ownerID, scope)) {
            if (playlist.getName().equalsIgnoreCase(name)) {
                return playlist;
            }
        }
        return null;
    }

    public static void savePlaylist(Playlist playlist) {
        Cascade.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, Playlist.class).insertOne(
                    playlist,
                    new DebugLogCallback<>("Inserted new playlist with ID: " + playlist.getPlaylistID())
            );
        });
    }

    public static void replacePlaylist(Playlist playlist) {
        Cascade.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, Playlist.class).replaceOne(
                    eq("_id", playlist.getPlaylistID()),
                    playlist,
                    new DebugLogCallback<>("Replaced Playlist with ID: " + playlist.getPlaylistID())
            );
        });
    }

}
