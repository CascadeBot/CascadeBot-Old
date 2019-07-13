/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;


import org.cascadebot.cascadebot.permissions.CascadePermission;

/**
 * Core commands cannot be overridden, given aliases or disabled. They also do not have permissions.
 */
public interface ICommandCore extends ICommandMain {

    @Override
    default boolean forceDefault() {
        return true;
    }

    @Override
    default Module getModule() {
        return Module.CORE;
    }

    @Override
    default CascadePermission getPermission() {
        return null;
    }

}
