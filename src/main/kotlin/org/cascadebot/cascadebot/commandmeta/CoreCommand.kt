/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta

import org.cascadebot.cascadebot.permissions.CascadePermission

abstract class CoreCommand : MainCommand() {

    override fun module(): Module = Module.CORE

    override fun permission(): CascadePermission? = null

}