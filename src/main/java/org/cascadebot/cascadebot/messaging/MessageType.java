/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cascadebot.cascadebot.Constants;
import org.cascadebot.cascadebot.UnicodeConstants;

import java.awt.Color;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    INFO(Constants.COLOR_INFO, UnicodeConstants.INFORMATION),
    SUCCESS(Constants.COLOR_SUCCESS, UnicodeConstants.TICK),
    WARNING(Constants.COLOR_WARNING, UnicodeConstants.WARNING),
    MODERATION(Constants.COLOR_MODERATION, UnicodeConstants.POLICE),
    DANGER(Constants.COLOR_DANGER, UnicodeConstants.RED_CROSS),
    NEUTRAL(Constants.COLOR_NEUTRAL, "");

    private final Color color;
    private final String emoji;

}
