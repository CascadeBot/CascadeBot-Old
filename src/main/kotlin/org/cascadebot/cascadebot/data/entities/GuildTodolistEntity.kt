/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.JoinColumn
import javax.persistence.JoinColumns
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "guild_todolist")
@IdClass(GuildTodolistId::class)
class GuildTodolistEntity(name: String, guildId: Long) {

    @Id
    @Column(name = "name", nullable = false)
    val name: String = name

    @Id
    @Column(name = "guild_id", nullable = false)
    val guildId: Long = guildId

    @Column(name = "message_id", nullable = true)
    val messageId: Long? = null

    @Column(name = "channel_id", nullable = true)
    val channelId: Long? = null

    @Column(name = "current_item", nullable = false)
    val currentItem: Int = 0

    @OneToMany()
    @Cascade(CascadeType.ALL)
    @JoinColumns(
        JoinColumn(name = "todolist_name", referencedColumnName = "name"),
        JoinColumn(name = "guild_id", referencedColumnName = "guild_id"),
    )
    val todolistItems: List<GuildTodolistItemEntity> = listOf()

}

data class GuildTodolistId(val name: String, val guildId: Long) : Serializable