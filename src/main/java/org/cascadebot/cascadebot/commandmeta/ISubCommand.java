/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import org.cascadebot.cascadebot.CascadeBot;

public interface ISubCommand extends ICommandExecutable {

    String parent();

    default ICommandMain getParent() {
        return CascadeBot.INS.getCommandManager().getCommand(parent());
    }

    @Override
    default String getDescriptionPath() {
        return "command_descriptions." + parent() + "." + command();
    }

}
