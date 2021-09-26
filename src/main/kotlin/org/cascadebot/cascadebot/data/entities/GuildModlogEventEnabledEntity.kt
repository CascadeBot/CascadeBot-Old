/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild_modlog_event_enabled")
class GuildModlogEventEnabledEntity(modlogId: UUID, channelId: Long, guildId: Long, event: String) {

    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()

    @Column(name = "modlog_id")
    val modlogId: UUID = modlogId

    @Column(name = "channel_id")
    val channelId: Long = channelId

    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "event", nullable = false)
    val event: String = event


}