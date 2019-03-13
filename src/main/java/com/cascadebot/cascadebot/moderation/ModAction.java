/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.moderation;

public enum ModAction {

    BAN("banned"),
    UNBAN("unbanned"),
    SOFT_BAN("soft-banned"), // Bans a user then unbans them, clears messages without actually banning them
    FORCE_BAN("force-banned"),
    KICK("kicked"),
    MUTE("muted"),
    WARN("warned");


    private String verb;

    ModAction(String verb) {
        this.verb = verb;
    }

    public String getVerb() {
        return verb;
    }

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", "-");
    }
}
