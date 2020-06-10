/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta

import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.shared.SecurityLevel

abstract class RestrictedCommand : MainCommand() {

    open fun commandLevel(): SecurityLevel = SecurityLevel.STAFF

    override fun permission(): CascadePermission? = null

}