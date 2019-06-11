/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commandmeta;

public enum ModuleFlag {

    /**
     * Indicates that this module must always be enabled for the proper function
     * of the bot.
     */
    REQUIRED,
    /**
     * Indicates that this module cannot be used by general users of the bot and
     * so is unable to be disabled
     */
    PRIVATE

}
