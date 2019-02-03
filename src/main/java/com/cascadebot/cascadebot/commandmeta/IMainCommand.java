/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import java.util.Set;

public interface IMainCommand extends ICommandExecutable {

    CommandType getType();

    default boolean forceDefault() {
        return false;
    }

    default Set<String> getGlobalAliases() {
        return Set.of();
    }

    default Set<ICommandExecutable> getSubCommands() { return Set.of(); }

}
