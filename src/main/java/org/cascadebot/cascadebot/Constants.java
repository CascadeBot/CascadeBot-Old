/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot;

import lombok.experimental.UtilityClass;
import org.cascadebot.shared.SharedConstants;

import java.awt.*;

@UtilityClass
public class Constants {

    // Changeable constants if needed
    public static String serverInvite = "https://discord.gg/P23GZFB";

    // Semantic colors used for embeds
    public static final Color COLOR_SUCCESS = new Color(132, 214, 162); // #84D6A2
    public static final Color COLOR_INFO = new Color(152, 159, 255); // #989FFF
    public static final Color COLOR_WARNING = new Color(255, 187, 95); // #FFBB5F
    public static final Color COLOR_DANGER = new Color(16741492); // #FF7474
    public static final Color COLOR_MODERATION = Color.WHITE;
    public static final Color COLOR_NEUTRAL = SharedConstants.CASCADE_COLOR;

}
