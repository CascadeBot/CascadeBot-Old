/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import java.awt.*;
import java.util.regex.Pattern;

public class Constants {

    public static final String CONFIG_VERSION = "1.0.0";

    public static final Pattern INTEGER_REGEX = Pattern.compile("-?[0-9]+");
    public static final Pattern DECIMAL_REGEX = Pattern.compile("-?[0-9]*([.,])[0-9]+");
    public static final Pattern MULTISPACE_REGEX = Pattern.compile(" {2,}");

    public static final Color CASCADE_COLOR = new Color(249, 163, 30); // #F9A31E

    public static final String GITHUB_URL = "https://github.com/CascadeBot/CascadeBot";

    public static final long OFFICIAL_SERVER_ID = 488394590458478602L;

    // Changeable constants if needed
    public static final String serverInvite = "https://discord.gg/UcmXMyH";



}
