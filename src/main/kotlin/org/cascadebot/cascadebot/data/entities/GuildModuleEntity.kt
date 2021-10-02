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
@Table(name = "guild_module")
class GuildModuleEntity(guildId: Long) {

    @Id
    @Column(name = "guild_id", nullable = false)
    val guildId: Long = guildId

    @Column(name = "core", nullable = false)
    val core: Boolean = true

    @Column(name = "useful", nullable = false)
    val useful: Boolean = true

    @Column(name = "moderation", nullable = false)
    val moderation: Boolean = true

    @Column(name = "management", nullable = false)
    val management: Boolean = true

    @Column(name = "informational", nullable = false)
    val informational: Boolean = true

}