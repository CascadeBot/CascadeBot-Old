/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

// I've created a new class because we may want extra
// logic here in the future such as a Patreon bot
public final class Environment {

    public static boolean isDevelopment() {
        return CascadeBot.getVersion().getBuild().equalsIgnoreCase("dev");
    }

    public static boolean isProduction() {
        return !isDevelopment();
    }

}
