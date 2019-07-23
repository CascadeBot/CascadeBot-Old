/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import de.bild.codec.annotations.Id;
import io.leangen.graphql.annotations.GraphQLIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Playlist {

    @Id
    @GraphQLIgnore
    private ObjectId playlistId = ObjectId.get();

    private String name;
    private long ownerId;
    private PlaylistScope scope;
    private List<String> tracks;

    public Playlist(String name, List<String> tracks, long ownerId, PlaylistScope scope) {
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

}
