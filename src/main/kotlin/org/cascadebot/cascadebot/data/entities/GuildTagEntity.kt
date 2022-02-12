/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "guild_tag")
@IdClass(GuildTagId::class)
class GuildTagEntity(guildId: Long, name: String, content: String) {

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Id
    @Column(name = "name")
    val name: String = name

    @Column(name = "content", nullable = false)
    var content: String = content

    @Column(name = "category")
    var category: String? = null

}

data class GuildTagId(val guildId: Long, val name: String) : Serializable {
    constructor() : this(0, "")
}
