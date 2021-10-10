/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild_autorole")
class GuildAutoRoleEntity(guildId: Long, roleId: Long) {

    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID()

    @Column(name = "guild_id", nullable = false)
    val guildId: Long = guildId

    @Column(name = "role_id", nullable = false)
    val roleId: Long = roleId

}