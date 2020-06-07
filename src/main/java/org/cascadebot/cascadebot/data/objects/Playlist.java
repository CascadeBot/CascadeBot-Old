/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import de.bild.codec.annotations.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Playlist {

    @Id
    private ObjectId playlistId = ObjectId.get();

    private String name;
    private long ownerId;
    private PlaylistType scope;

    @Setter
    private List<String> tracks;

    public Playlist(long ownerId, String name, PlaylistType scope, List<String> tracks) {
        this.ownerId = ownerId;
        this.name = name;
        this.scope = scope;
        this.tracks = tracks;
    }

    public void addTrack(String url) {
        tracks.add(url);
    }

    public void removeTrack(String url) {
        tracks.remove(url);
    }

    public String getName() {
        return name;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public PlaylistType getType() {
        return scope;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public ObjectId getPlaylistId() {
        return playlistId;
    }

}
