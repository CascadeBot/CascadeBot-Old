/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild_filter_criteria")
class GuildFilterCriteriaEntity(filterName: String, guildId: Long, targetType: FilterTargetType, targetId: Long) {

    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID()

    @Column(name = "filter_name", nullable = false)
    val filterName: String = filterName

    @Column(name = "guild_id", nullable = false)
    val guildId: Long = guildId

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "psql-enum")
    val targetType: FilterTargetType = targetType

    @Column(name = "target_id", nullable = false)
    val targetId: Long = targetId

}

enum class FilterTargetType {
    CHANNEl, ROLE, USER
}