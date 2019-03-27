/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import de.bild.codec.annotations.Id;

import java.util.List;
import java.util.UUID;

public class Playlist {

    @Id
    private UUID playlistID = UUID.randomUUID();

    private String name;
    private long ownerID;
    private PlaylistType scope;

    private List<String> tracks;

    private Playlist() {}

    public Playlist(long ownerId, String name, PlaylistType scope, List<String> tracks) {
        this.ownerID = ownerId;
        this.name = name;
        this.scope = scope;
        this.tracks = tracks;
    }

    public UUID getPlaylistID() {
        return playlistID;
    }

    public String getName() {
        return name;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public PlaylistType getScope() {
        return scope;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public void addTack(String url) {
        tracks.add(url);
    }

    public void removeTrack(String url) {
        tracks.remove(url);
    }

    public void setTracks(List<String> tracks) {
        this.tracks = tracks;
    }
}
