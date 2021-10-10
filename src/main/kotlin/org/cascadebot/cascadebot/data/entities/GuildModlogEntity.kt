/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.JoinColumn
import javax.persistence.JoinColumns
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "guild_modlog")
@IdClass(GuildModlogId::class)
class GuildModlogEntity(guildId: Long, channelId: Long) {

    @Id
    @Column(name = "id")
    @GeneratedValue
    val id: UUID = UUID.randomUUID()

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Id
    @Column(name = "channel_id")
    val channelId: Long = channelId

    @Column(name = "webhook_id")
    var webhookId: Long? = null

    @Column(name = "webhook_token")
    var webhookToken: String? = null

    @OneToMany()
    @Cascade(CascadeType.ALL)
    @JoinColumns(
        JoinColumn(name = "id", referencedColumnName = "modlog_id"),
        JoinColumn(name = "guild_id", referencedColumnName = "guild_id"),
        JoinColumn(name = "channel_id", referencedColumnName = "channel_id")
    )
    val eventsEnabled: List<GuildModlogEventEnabledEntity> = listOf()

}

data class GuildModlogId(val id: UUID, val guildId: Long, val channelId: Long) : Serializable