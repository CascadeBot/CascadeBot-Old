package org.cascadebot.cascadebot.data.entities

import org.cascadebot.cascadebot.data.objects.CommandFilter
import org.hibernate.annotations.Type
import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "guild_filter")
@IdClass(GuildFilterId::class)
class GuildFilterEntity(
    name: String,
    guildId: Long,
    type: CommandFilter.FilterType,
    operator: CommandFilter.FilterOperator
) {

    @Id
    @Column(name = "name")
    val name: String = name

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true

    @Column(name = "type", nullable = false)
    @Type(type = "psql-enum")
    @Enumerated(EnumType.STRING)
    var type: CommandFilter.FilterType = type

    @Column(name = "operator", nullable = false)
    @Type(type = "psql-enum")
    @Enumerated(EnumType.STRING)
    var operator: CommandFilter.FilterOperator = operator

    @Column(name = "commands", columnDefinition = "varchar(255)[]", nullable = false)
    @Type(type = "list-array")
    val commands: MutableList<String> = mutableListOf()

}

data class GuildFilterId(val name: String, val guildId: Long) : Serializable {
    constructor() : this("", 0)
}