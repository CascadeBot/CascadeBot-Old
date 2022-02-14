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
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "core", nullable = false)
    var core: Boolean = true

    @Column(name = "useful", nullable = false)
    var useful: Boolean = true

    @Column(name = "moderation", nullable = false)
    var moderation: Boolean = true

    @Column(name = "management", nullable = false)
    var management: Boolean = true

    @Column(name = "informational", nullable = false)
    var informational: Boolean = true

}