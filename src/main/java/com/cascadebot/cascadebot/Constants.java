/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.shared.Version;

import java.util.regex.Pattern;

public final class Constants {

    public static final Version CONFIG_VERSION = Version.of(1, 0, 0);

    public static final Pattern INTEGER_REGEX = Pattern.compile("-?[0-9]+");
    public static final Pattern DECIMAL_REGEX = Pattern.compile("-?[0-9]*([.,])[0-9]+");
    public static final Pattern MULTISPACE_REGEX = Pattern.compile(" {2,}");

    // Changeable constants if needed
    public static final String serverInvite = "https://discord.gg/UcmXMyH";


}
