/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commandmeta;

import java.util.Set;

public interface ICommandMain extends ICommandExecutable {

    Module getModule();

    default boolean forceDefault() {
        return false;
    }

    default Set<String> getGlobalAliases() {
        return Set.of();
    }

    default Set<ICommandExecutable> getSubCommands() { return Set.of(); }

}
