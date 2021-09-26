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
@Table(name = "guild_modules")
class GuildModulesEntity(guildId: Long) {

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "core")
    val core: Boolean = true

    @Column(name = "useful")
    val useful: Boolean = true

    @Column(name = "moderation")
    val moderation: Boolean = true

    @Column(name = "management")
    val management: Boolean = true

    @Column(name = "informational")
    val informational: Boolean = true

}