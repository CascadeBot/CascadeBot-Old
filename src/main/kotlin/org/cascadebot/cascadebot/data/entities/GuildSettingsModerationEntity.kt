/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.objects.Setting
import org.cascadebot.cascadebot.data.objects.SettingsContainer
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@SettingsContainer(module = Module.MODERATION)
@Table(name = "guild_settings_moderation")
class GuildSettingsModerationEntity(guildId: Long) {

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "purge_pinned", nullable = false)
    @Setting
    var purgePinned: Boolean = true

    @Column(name = "respect_hierarchy", nullable = false)
    @Setting
    var respectHierarchy: Boolean = true

    @Column(name = "mute_role_name", nullable = false)
    var muteRoleName: String = "Muted"

    @Column(name = "mute_role_id", nullable = false)
    var muteRoleId: Long? = null

}