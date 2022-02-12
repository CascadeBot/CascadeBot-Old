package org.cascadebot.cascadebot.data.entities

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.managers.ScheduledActionManager
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table


@Entity
@Table(name = "scheduled_action")
class ScheduledActionEntity(
    type: ActionType,
    data: ActionData,
    guildId: Long,
    channelId: Long,
    userId: Long,
    creationTime: Instant,
    executionTime: Instant
) : Runnable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int = 0

    val delay: Long
        get() = ChronoUnit.MILLIS.between(Instant.now(), executionTime).coerceAtLeast(0L)

    val guild: Guild?
        get() = CascadeBot.INS.shardManager.getGuildById(guildId)

    val user: User?
        get() = CascadeBot.INS.shardManager.getUserById(userId)

    val channel: TextChannel?
        get() = CascadeBot.INS.shardManager.getTextChannelById(channelId)

    @Column(name = "type")
    val type: ActionType = type

    @OneToOne
    @JoinColumn(name = "data_id", referencedColumnName = "id")
    val data: ActionData = data

    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "channel_id")
    val channelId: Long = channelId

    @Column(name = "user_id")
    val userId: Long = userId

    @Column(name = "creation_time")
    val creationTime: Instant = creationTime

    @Column(name = "execution_time")
    val executionTime: Instant = executionTime

    constructor(
        type: ActionType,
        data: ActionData,
        guildId: Long,
        channelId: Long,
        userId: Long,
        creationTime: Instant,
        delay: Long
    ) :
            this(type, data, guildId, channelId, userId, creationTime, creationTime.plus(delay, ChronoUnit.MILLIS)!!)

    override fun run() {
        try {
            type.dataConsumer(this)
        } finally {
            ScheduledActionManager.deleteScheduledAction(this.id)
        }
    }

    @Entity
    @Table(name = "scheduled_action_data")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    open class ActionData {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private val id: Int = 0

    }

    @Entity
    class ModerationActionData(targetId: Long) : ActionData() {

        @Column(name = "target_id")
        val targetId: Long = targetId

    }

    @Entity
    class ReminderActionData(reminder: String, isDM: Boolean) : ActionData() {

        @Column(name = "reminder")
        val reminder: String = reminder

        @Column(name = "is_dm")
        val isDM: Boolean = false

    }

    @Entity
    class SlowmodeActionData(targetId: Long, oldSlowmode: Int) : ActionData() {

        @Column(name = "target_id")
        val targetId: Long = targetId

        @Column(name = "old_slowmode")
        val oldSlowmode: Int = oldSlowmode

    }

}
