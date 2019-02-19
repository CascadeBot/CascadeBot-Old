/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import java.util.Set;

public enum Module {
    MODERATION,
    CORE,
    FUN,
    INFORMATIONAL,
    MANAGEMENT,
    DEVELOPER(false);

    // A set of modules that are always enabled and cannot be disabled
    public static final Set<Module> CORE_MODULES = Set.of(Module.CORE, Module.MANAGEMENT);

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
