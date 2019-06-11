/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade;

import lombok.experimental.UtilityClass;

// I've created a new class because we may want extra
// logic here in the future such as a Patreon bot
@UtilityClass
public class Environment {

    public static boolean isDevelopment() {
        return Cascade.getVersion().getBuild().equalsIgnoreCase("dev");
    }

    public static boolean isProduction() {
        return !isDevelopment();
    }

}
