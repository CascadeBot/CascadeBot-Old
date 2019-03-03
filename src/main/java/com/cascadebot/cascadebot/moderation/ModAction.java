/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.moderation;

public enum ModAction {

    BAN("banned"),
    UNBAN("unbanned"),
    SOFT_BAN("soft-banned"), // Bans a user then unbans them, clears messages without actually banning them
    FORCE_BAN("force-banned",false),
    KICK("kicked"),
    MUTE("muted"),
    WARN("warned");


    // This determines whether we can do this on any user or on a member of the guild only.
    private boolean needsMember;
    private String verb;

    ModAction(String verb) {
        this.verb = verb;
        this.needsMember = true;
    }

    ModAction(String verb, boolean needsMember) {
        this.verb = verb;
        this.needsMember = needsMember;
    }

    public boolean needsMember() {
        return needsMember;
    }

    public String getVerb() {
        return verb;
    }

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", "-");
    }
}
