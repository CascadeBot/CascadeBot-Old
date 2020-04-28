package org.cascadebot.cascadebot.messaging

import org.cascadebot.cascadebot.Constants
import org.cascadebot.cascadebot.UnicodeConstants
import java.awt.Color

enum class MessageType(val color: Color, val emoji: String) {

    INFO(Constants.COLOR_INFO, UnicodeConstants.INFORMATION),
    SUCCESS(Constants.COLOR_SUCCESS, UnicodeConstants.TICK),
    WARNING(Constants.COLOR_WARNING, UnicodeConstants.WARNING),
    MODERATION(Constants.COLOR_MODERATION, UnicodeConstants.POLICE),
    DANGER(Constants.COLOR_DANGER, UnicodeConstants.RED_CROSS),
    NEUTRAL(Constants.COLOR_NEUTRAL, "")

}