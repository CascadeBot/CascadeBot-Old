/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.messaging;

import com.cascadebot.shared.SharedConstants;

import java.awt.*;

public enum MessageType {

    INFO(Color.CYAN, "\u2139"), // ℹ
    SUCCESS(Color.GREEN, "\u2705"), // ✅
    WARNING(Color.YELLOW, "\u26A0"), // ⚠
    MODERATION(Color.WHITE, "\uD83D\uDC6E"), // 👮
    DANGER(Color.RED, "\u274C"), // ❌
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
