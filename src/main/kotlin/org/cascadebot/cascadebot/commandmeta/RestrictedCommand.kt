/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta

import org.cascadebot.shared.SecurityLevel

abstract class RestrictedCommand : MainCommand() {

    open fun commandLevel(): SecurityLevel = SecurityLevel.STAFF

}