/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

public enum CommandType {

    CORE,
    DEVELOPER(false);

    private boolean availableModule;

    CommandType() {
        this.availableModule = true;
    }

    CommandType(boolean availableModule) {
        this.availableModule = availableModule;
    }

    public boolean isAvailableModule() {
        return availableModule;
    }
}
