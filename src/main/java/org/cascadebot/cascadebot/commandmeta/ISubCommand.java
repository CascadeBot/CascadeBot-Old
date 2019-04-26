/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

public interface ISubCommand extends ICommandExecutable {

    String parent();

    @Override
    default String getDescriptionPath() {
        return "command_descriptions." + parent() + "." + command();
    }

}
