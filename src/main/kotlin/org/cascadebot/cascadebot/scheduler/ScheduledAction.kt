package org.cascadebot.cascadebot.scheduler

import org.bson.types.ObjectId
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

data class ScheduledAction(
        val type: ActionType,
        val data: ActionData,
        val guildId: Long,
        val channelId: Long,
        val userId: Long,
        val creationTime: OffsetDateTime,
        val executionTime: OffsetDateTime
) : Runnable {

    val id: ObjectId = ObjectId()

    val delay: Long
        get() = ChronoUnit.MILLIS.between(creationTime, executionTime)

    // Default constructor for MongoDB serialisation
    private constructor() : this(
            ActionType.REMINDER,
            object : ActionData {},
            0L,
            0L,
            0L,
            OffsetDateTime.now(),
            0L
    )

    constructor(
            type: ActionType,
            data: ActionData,
            guildId: Long,
            channelId: Long,
            userId: Long,
            creationTime: OffsetDateTime,
            delay: Long) :
            this(type, data, guildId, channelId, userId, creationTime, creationTime.plus(delay, ChronoUnit.MILLIS)!!)

    override fun run() {
        type.dataConsumer(this)
    }

    enum class ActionType(val dataConsumer: (ScheduledAction) -> Unit) {
        UNMUTE({ action -> TODO() }),
        UNBAN({ action -> TODO() }),
        REMINDER({ action -> TODO() });

        fun verifyDataType(data: ActionData): Boolean {
            return when (this) {
                UNMUTE -> data is ModerationActionData
                UNBAN -> data is ModerationActionData
                REMINDER -> data is ReminderActionData
            }
        }
    }

    interface ActionData

    class ModerationActionData(val targetId: Long) : ActionData
    class ReminderActionData(val reminder: String, val isDM: Boolean = false) : ActionData

}
