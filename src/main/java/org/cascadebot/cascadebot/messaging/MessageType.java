/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.shared.SharedConstants;

import java.awt.Color;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    INFO(Color.CYAN, UnicodeConstants.INFORMATION),
    SUCCESS(Color.GREEN, UnicodeConstants.TICK),
    WARNING(Color.YELLOW, UnicodeConstants.WARNING),
    MODERATION(Color.WHITE, UnicodeConstants.POLICE),
    DANGER(Color.RED, UnicodeConstants.RED_CROSS),
    NEUTRAL(SharedConstants.CASCADE_COLOR, "");

    private final Color color;
    private final String emoji;

}
