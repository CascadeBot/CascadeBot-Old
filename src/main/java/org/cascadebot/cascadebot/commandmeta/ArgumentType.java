/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

public enum ArgumentType {

    /**
     * Represents an optional parameter.
     */
    OPTIONAL,
    /**
     * Represents an required parameter.
     */
    REQUIRED,
    /**
     * Represents that this is just part of the command and isn't a parameter.
     */
    COMMAND

}
