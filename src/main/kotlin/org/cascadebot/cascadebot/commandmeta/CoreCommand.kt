/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta

abstract class CoreCommand : MainCommand() {

    override fun module(): Module = Module.CORE

}