/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "guild_modlog")
@IdClass(GuildModlogId::class)
class GuildModlogEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue
    val id: Int = 0

    @Id
    @Column(name = "guild_id")
    val guildId: Long = 0

    @Id
    @Column(name = "channel_id")
    val channelId: Long = 0

    @Column(name = "webhook_id")
    val webhookId: Long? = null

    @Column(name = "webhook_token")
    val webhookToken: String? = null

}

data class GuildModlogId(val id: Int, val guildId: Long, val channelId: Long) : Serializable