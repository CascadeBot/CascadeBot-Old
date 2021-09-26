/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.hibernate.cfg.Environment
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild")
class GuildEntity(guildId: Long) {

    @Id
    @Column(name = "guild_id")
    var guildId: Long = guildId

    @Column(name = "created_at")
    val createdAt: LocalDateTime? = LocalDateTime.now()

    @Column(name = "removed_at")
    var removedAt: LocalDateTime? = null

}