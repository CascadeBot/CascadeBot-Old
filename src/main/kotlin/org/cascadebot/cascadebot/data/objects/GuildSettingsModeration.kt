/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects

import org.cascadebot.cascadebot.commandmeta.Module

@SettingsContainer(module = Module.MODERATION)
class GuildSettingsModeration {

    @Setting
    var purgePinnedMessages = false

    @Setting
    var respectBanOrKickHierarchy = true

    @Setting
    var muteRoleName = "Muted"

}
