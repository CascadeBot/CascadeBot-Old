/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

public enum Module {

    CORE,
    FUN,
    INFORMATIONAL,
    MANAGEMENT,
    DEVELOPER(false);

    private boolean publicModule;

    Module() {
        this.publicModule = true;
    }

    Module(boolean publicModule) {
        this.publicModule = publicModule;
    }

    public boolean isPublicModule() {
        return publicModule;
    }
}
