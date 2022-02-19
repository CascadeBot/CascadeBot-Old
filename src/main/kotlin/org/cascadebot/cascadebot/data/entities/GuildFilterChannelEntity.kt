package org.cascadebot.cascadebot.data.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "guild_filter_channels")
@IdClass(GuildFilterChanelId::class)
class GuildFilterChannelEntity(
    filterName: String,
    guildId: Long,
    channelId: Long
) {

    @Id
    @Column(name = "filter_name")
    val filterName = filterName

    @Id
    @Column(name = "guild_id")
    val guildId = guildId

    @Id
    @Column(name = "channel_id")
    val channelId = channelId

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GuildFilterChannelEntity

        if (filterName != other.filterName) return false
        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filterName.hashCode()
        result = 31 * result + guildId.hashCode()
        result = 31 * result + channelId.hashCode()
        return result
    }

}

data class GuildFilterChanelId(val filterName: String, val guildId: Long, val channelId: Long) : Serializable {
    constructor() : this("", 0, 0)
}