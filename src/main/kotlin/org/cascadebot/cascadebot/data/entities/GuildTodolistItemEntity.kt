package org.cascadebot.cascadebot.data.entities

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild_todolist_item")
class GuildTodolistItemEntity(todolistName: String, guildId: Long, text: String) {

    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID()

    @Column(name = "todolist_name", nullable = false)
    val todolistName: String = todolistName

    @Column(name = "guild_id", nullable = false)
    val guildId: Long = guildId

    @Column(name = "text", nullable = false)
    var text: String = text

    @Column(name = "done", nullable = false)
    var done: Boolean = false

}