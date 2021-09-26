/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

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

    @Column(name = "display_filter_error")
    val displayFilterError: Boolean = true

    @Column(name = "warn_over_10")
    val warnOver10: Boolean = true

}