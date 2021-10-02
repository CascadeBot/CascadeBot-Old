/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.cascadebot.cascadebot.data.objects.GreetingType
import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild_greeting")
class GuildGreetingEntity(guildId: Long, type: GreetingType, content: String) {

    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID()

    @Column(name = "guild_id", nullable = false)
    val guildId: Long = guildId

    @Column(name = "type", nullable = false)
    @Type(type = "org.cascadebot.cascadebot.data.EnumDBType")
    @Enumerated(EnumType.STRING)
    var type: GreetingType = type

    @Column(name = "content", nullable = false)
    var content: String = content

    @Column(name = "weight", nullable = false)
    var weight: Int = 1

}