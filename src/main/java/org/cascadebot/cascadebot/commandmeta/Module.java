/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum Module {

    /**
     * A module that contains the bot's meta commands. These cannot be disabled!
     */
    CORE(ModuleFlag.REQUIRED, ModuleFlag.DEFAULT),
    /**
     * This module contains commands that are used to manage the bot settings for the guild.
     */
    MANAGEMENT(ModuleFlag.REQUIRED, ModuleFlag.DEFAULT),
    /**
     * This is music, what do you think this is?
     */
    MUSIC(ModuleFlag.DEFAULT),
    /**
     * The commands in this module display various pieces of information about discord entities.
     */
    INFORMATIONAL(ModuleFlag.DEFAULT),
    /**
     * This module speaks for itself, it contains commands that allow the admins of a guild to moderate said guild
     */
    MODERATION(ModuleFlag.DEFAULT),
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

    private boolean isFlagEnabled(ModuleFlag flag) {
        return this.flags.contains(flag);
    }

    public boolean isPrivate() {
        return isFlagEnabled(ModuleFlag.PRIVATE);
    }

    public boolean isRequired() {
        return isFlagEnabled(ModuleFlag.REQUIRED);
    }

    public boolean isDefault() {
        return isFlagEnabled(ModuleFlag.DEFAULT);
    }

    public static Set<Module> getModules(ModuleFlag... flags) {
        return Arrays.stream(Module.values()).filter(module -> module.getFlags().containsAll(Arrays.asList(flags))).collect(Collectors.toSet());
    }


}
