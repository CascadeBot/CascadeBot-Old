/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.objects.PermissionMode
import org.cascadebot.cascadebot.data.objects.Setting
import org.cascadebot.cascadebot.data.objects.SettingsContainer
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@SettingsContainer(module = Module.MANAGEMENT)
@Table(name = "guild_settings_management")
class GuildSettingsManagementEntity(guildId: Long) {

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "warn_over_10", nullable = false)
    @Setting
    var warnOver10: Boolean = true

}