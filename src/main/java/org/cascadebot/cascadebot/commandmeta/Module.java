/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import java.util.Set;

public enum Module {

    CORE, // A module that contains the bot's meta commands. These cannot be disabled!
    MANAGEMENT, // This module contains commands that are used to manage the bot settings for the guild.
    INFORMATIONAL, // The commands in this module display various pieces of information about discord entities.
    MUSIC, // This is music, what do you think this is?
    MODERATION, // This module speaks for itself, it contains commands that allow the admins of a guild to moderate said guild
    FUN, // This is a bit of a random module containing smaller commands.
    DEVELOPER(false); // All our special commands :D

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
