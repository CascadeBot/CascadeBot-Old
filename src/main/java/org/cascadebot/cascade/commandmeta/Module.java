/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commandmeta;

import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;

public enum Module {

    /**
     * A module that contains the bot's meta commands. These cannot be disabled!
     */
    CORE(ModuleFlag.REQUIRED),
    /**
     * This module contains commands that are used to manage the bot settings for the guild.
     */
    MANAGEMENT(ModuleFlag.REQUIRED),
    /**
     * This is music, what do you think this is?
     */
    MUSIC,
    /**
     * The commands in this module display various pieces of information about discord entities.
     */
    INFORMATIONAL,
    /**
     * This module speaks for itself, it contains commands that allow the admins of a guild to moderate said guild
     */
    MODERATION,
    /**
     * This is a bit of a random module containing smaller commands.
     */
    FUN,
    /**
     * All our special commands :D
     */
    DEVELOPER(ModuleFlag.PRIVATE);

    @Getter
    private EnumSet<ModuleFlag> flags;

    Module() {
        this.flags = EnumSet.noneOf(ModuleFlag.class); // Public module that is not required
    }

    Module(ModuleFlag... flags) {
        this.flags = EnumSet.noneOf(ModuleFlag.class);
        this.flags.addAll(Arrays.asList(flags));
    }

    public boolean isFlagEnabled(ModuleFlag flag) {
        return this.flags.contains(flag);
    }


}
