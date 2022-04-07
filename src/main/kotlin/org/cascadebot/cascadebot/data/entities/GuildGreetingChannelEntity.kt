package org.cascadebot.cascadebot.data.entities;

import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity;
import javax.persistence.Id
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "guild_greeting_channel")
class GuildGreetingChannelEntity(guildId: Long, channelId: Long?) {

    @Id
    @Column(name = "guild_id", nullable = false)
    val guildId: Long = guildId

    @Column(name = "channel_id", nullable = true)
    val channelId: Long? = channelId;

}
