package org.cascadebot.cascadebot.data.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "guild_filter_users")
@IdClass(GuildFilterUserId::class)
class GuildFilterUserEntity(
    filterName: String,
    guildId: Long,
    userId: Long
) {

    @Id
    @Column(name = "filter_name")
    val filterName = filterName

    @Id
    @Column(name = "guild_id")
    val guildId = guildId

    @Id
    @Column(name = "user_id")
    val userId = userId

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GuildFilterUserEntity

        if (filterName != other.filterName) return false
        if (guildId != other.guildId) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filterName.hashCode()
        result = 31 * result + guildId.hashCode()
        result = 31 * result + userId.hashCode()
        return result
    }

}

data class GuildFilterUserId(val filterName: String, val guildId: Long, val userId: Long) : Serializable {
    constructor() : this("", 0, 0)
}