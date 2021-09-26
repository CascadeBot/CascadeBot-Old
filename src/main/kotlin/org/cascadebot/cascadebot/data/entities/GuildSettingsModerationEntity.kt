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
@Table(name = "guild_settings_moderation")
class GuildSettingsModerationEntity(guildId: Long) {

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "purge_pinned")
    var purgePinned: Boolean = true

    @Column(name = "respect_hierarchy")
    var respectHierarchy: Boolean = true

    @Column(name = "mute_role_name")
    var muteRoleName: String = "Muted"

    @Column(name = "mute_role_id")
    var muteRoleId: Long? = null

}