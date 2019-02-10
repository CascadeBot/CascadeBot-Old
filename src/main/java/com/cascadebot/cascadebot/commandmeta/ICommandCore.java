/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;


import com.cascadebot.cascadebot.permissions.Permission;

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
    default Permission getPermission() {
        return null;
    }

}
