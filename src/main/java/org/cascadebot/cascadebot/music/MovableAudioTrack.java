/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import org.cascadebot.cascadebot.utils.move.MovableItem;

public class MovableAudioTrack implements MovableItem {

    @Getter
    private AudioTrack track;

    public MovableAudioTrack(AudioTrack track) {
        this.track = track;
    }

    @Override
    public String getItemText() {
        return track.getInfo().title;
    }

    @Override
    public boolean equals(Object obj) { // I need to override the equals operator so its checking if the audi tracks are equal, not if the movable one is
        if (obj instanceof MovableAudioTrack) {
            return track.equals(((MovableAudioTrack) obj).getTrack());
        }
        return false;
    }
}
