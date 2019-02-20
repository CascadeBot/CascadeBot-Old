/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

public enum ArgumentType {
    /**
     * Represents an option param
     */
    OPTIONAL,
    /**
     * Represents an required param
     */
    REQUIRED,
    /**
     * Represents that this is just part of the command and isn't a pram
     */
    COMMAND
}
