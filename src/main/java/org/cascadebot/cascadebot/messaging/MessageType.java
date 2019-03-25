/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import org.cascadebot.shared.SharedConstants;

import java.awt.Color;

public enum MessageType {

    INFO(Color.CYAN, "\u2139"), // ℹ Information icon
    SUCCESS(Color.GREEN, "\u2705"), // ✅ Tick
    WARNING(Color.YELLOW, "\u26A0"), // ⚠ Warning symbol
    MODERATION(Color.WHITE, "\uD83D\uDC6E"), // 👮 Police symbol
    DANGER(Color.RED, "\u274C"), // ❌ Red cross
    NEUTRAL(SharedConstants.CASCADE_COLOR, "");

    private final Color color;
    private final String emoji;

    MessageType(Color color, String emoji) {
        this.color = color;
        this.emoji = emoji;
    }

    public Color getColor() {
        return color;
    }

    public String getEmoji() {
        return emoji;
    }

}
