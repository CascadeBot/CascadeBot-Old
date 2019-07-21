/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.managers;

import com.mongodb.client.MongoIterable;
import org.bson.types.ObjectId;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.database.DebugLogCallback;
import org.cascadebot.cascadebot.data.objects.Playlist;
import org.cascadebot.cascadebot.data.objects.PlaylistScope;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public final class PlaylistManager {

    private static final String COLLECTION = "playlists";

    public static Playlist getPlaylistById(String id) {
        if (!ObjectId.isValid(id)) return null;
        return CascadeBot.INS.getDatabaseManager().getDatabase().getCollection(COLLECTION, Playlist.class)
                .find(eq("id", new ObjectId(id)))
                .first();
    }

    public static MongoIterable<Playlist> getPlaylists(long ownerId, PlaylistScope scope) {
        return CascadeBot.INS.getDatabaseManager().getDatabase().getCollection(COLLECTION, Playlist.class)
                .find(
                        combine(
                                eq("ownerId", ownerId),
                                eq("scope", scope)
                        )
                );
    }

    public static Playlist getPlaylistByName(long ownerId, PlaylistScope scope, String name) {
        for (Playlist playlist : getPlaylists(ownerId, scope)) {
            if (playlist.getName().equalsIgnoreCase(name)) {
                return playlist;
            }
        }
        return null;
    }

    public static void savePlaylist(Playlist playlist) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, Playlist.class).insertOne(
                    playlist,
                    new DebugLogCallback<>("Inserted new playlist with ID: " + playlist.getPlaylistId())
            );
        });
    }

    public static void replacePlaylist(Playlist playlist) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, Playlist.class).replaceOne(
                    eq("_id", playlist.getPlaylistId()),
                    playlist,
                    new DebugLogCallback<>("Replaced Playlist with ID: " + playlist.getPlaylistId())
            );
        });
    }

}
