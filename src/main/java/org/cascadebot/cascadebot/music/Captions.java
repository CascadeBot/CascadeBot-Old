/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@ToString
public class Captions {

    private static final int CAPTION_RANGE = 15;

    private TreeSet<Caption> captionSet = new TreeSet<>(Comparator.comparing(Caption::getStart));

    public List<String> getCaptions(double position) {
        List<String> captions = new ArrayList<>();
        for (Caption caption : captionSet) {
            if (caption.start >= position && (caption.start + caption.duration) < (position + CAPTION_RANGE)) {
                captions.add(caption.text);
            }
        }
        return captions;
    }

    public void addCaption(String text, double start, double duration) {
        captionSet.add(new Caption(text, start, duration));
    }

    @AllArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    public class Caption {

        private final String text;
        private final double start;
        private final double duration;

    }

}
