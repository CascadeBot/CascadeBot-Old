package org.cascadebot.cascadebot.data.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "guild_filter_roles")
@IdClass(GuildFilterRoleId::class)
class GuildFilterRoleEntity(
    filterName: String,
    guildId: Long,
    roleId: Long
) {

    @Id
    @Column(name = "filter_name")
    val filterName = filterName

    @Id
    @Column(name = "guild_id")
    val guildId = guildId

    @Id
    @Column(name = "role_id")
    val roleId = roleId

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GuildFilterRoleEntity

        if (filterName != other.filterName) return false
        if (guildId != other.guildId) return false
        if (roleId != other.roleId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filterName.hashCode()
        result = 31 * result + guildId.hashCode()
        result = 31 * result + roleId.hashCode()
        return result
    }

}

data class GuildFilterRoleId(val filterName: String, val guildId: Long, val roleId: Long) : Serializable {
    constructor() : this("", 0, 0)
}