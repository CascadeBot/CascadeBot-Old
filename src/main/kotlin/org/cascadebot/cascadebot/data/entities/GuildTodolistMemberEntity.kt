package org.cascadebot.cascadebot.data.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "guild_todolist_member")
@IdClass(GuildTodolistMemberId::class)
class GuildTodolistMemberEntity(todolistName: String, guildId: Long, memberId: Long) {

    @Id
    @Column(name = "todolist_name", nullable = false)
    val todolistName: String = todolistName

    @Id
    @Column(name = "guild_id", nullable = false)
    val guildId: Long = guildId

    @Id
    @Column(name = "member", nullable = false)
    val memberId: Long = memberId

}

data class GuildTodolistMemberId(val todolistName: String, val guildId: Long, val memberId: Long) : Serializable {
    constructor() : this("", 0, 0)
}