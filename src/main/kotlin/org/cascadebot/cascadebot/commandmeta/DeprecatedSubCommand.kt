/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta

import org.cascadebot.cascadebot.CascadeBot

abstract class DeprecatedSubCommand : DeprecatedExecutableCommand() {

    abstract fun parent(): String

    fun getParent() = CascadeBot.INS.commandManager.getCommand(parent())

}