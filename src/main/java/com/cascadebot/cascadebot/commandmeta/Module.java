/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

public enum Module {

    CORE,
    FUN,
    INFORMATIONAL,
    DEVELOPER(false);

    private boolean availableModule;

    Module() {
        this.availableModule = true;
    }

    Module(boolean availableModule) {
        this.availableModule = availableModule;
    }

    public boolean isAvailableModule() {
        return availableModule;
    }
}
