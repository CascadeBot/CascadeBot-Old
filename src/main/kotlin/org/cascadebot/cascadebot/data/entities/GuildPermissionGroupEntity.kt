/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.apache.commons.lang3.RandomStringUtils
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import org.hibernate.annotations.Type
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "guild_permission_group")
@IdClass(GuildPermissionGroupId::class)
class GuildPermissionGroupEntity(name: String, guildId: Long) {

    @Id
    @Column(name = "group_id")
    // Base 55 with 5 chars gives 503284375 combinations, we should be ok for uniqueness
    // This is normal alphanumeric with similar characters removed for less errors when inputting
    val id: String = RandomStringUtils.random(5, "abcdefghijkmnopqrstuvwxyzACDEFHJKLMNPRSTUVWXYZ123467890")

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "name", nullable = false)
    var name: String = name

    @Column(name = "permissions", columnDefinition = "varchar(255)[]", nullable = false)
    @Type(type = "list-array")
    val permissions: MutableList<String> = mutableListOf()

    @Column(name = "roles", columnDefinition = "bigint[]", nullable = false)
    @Type(type = "list-array")
    val roles: MutableList<String> = mutableListOf()

    @ManyToMany
    @Cascade(CascadeType.ALL)
    @JoinTable(
        name = "guild_permission_user_membership",
        joinColumns = [JoinColumn(name = "group_id"), JoinColumn(name = "guild_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id"), JoinColumn(name = "user_guild_id")]
    )
    val users: MutableSet<GuildPermissionUserEntity> = mutableSetOf()

}

data class GuildPermissionGroupId(val id: String, val guildId: Long) : Serializable {
    constructor(): this("", 0)
}