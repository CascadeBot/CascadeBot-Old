/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.hibernate.annotations.Type
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "guild_permission_user")
@IdClass(GuildPermissionUserId::class)
class GuildPermissionUserEntity(id: Long, guildId: Long) {

    @Id
    @Column(name = "user_id")
    val id: Long = id

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "permissions", columnDefinition = "varchar(255)[]", nullable = false)
    @Type(type = "list-array")
    val permissions: MutableList<String> = mutableListOf()

    @ManyToMany(mappedBy = "users")
    val groups: MutableSet<GuildPermissionGroupEntity> = mutableSetOf()

}

data class GuildPermissionUserId(val id: Long, val guildId: Long) : Serializable