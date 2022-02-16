/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.cascadebot.cascadebot.data.objects.PermissionMode
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild_settings_management")
class GuildSettingsManagementEntity(guildId: Long) {

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "display_filter_error", nullable = false)
    var displayFilterError: Boolean = true

    @Column(name = "permission_mode")
    var permissionMode: PermissionMode = PermissionMode.MOST_RESTRICTIVE

    @Column(name = "warn_over_10", nullable = false)
    var warnOver10: Boolean = true

}