package org.cascadebot.cascadebot.scheduler

import de.bild.codec.annotations.Id
import org.bson.types.ObjectId
import org.cascadebot.cascadebot.data.managers.ScheduledActionManager
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

data class ScheduledAction(
        val type: ActionType,
        val data: ActionData,
        val guildId: Long,
        val channelId: Long,
        val userId: Long,
        val creationTime: Instant,
        val executionTime: Instant
) : Runnable {

    @Id
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
            Instant.now(),
            0L
    )

    constructor(
            type: ActionType,
            data: ActionData,
            guildId: Long,
            channelId: Long,
            userId: Long,
            creationTime: Instant,
            delay: Long) :
            this(type, data, guildId, channelId, userId, creationTime, creationTime.plus(delay, ChronoUnit.MILLIS)!!)

    override fun run() {
        try {
            type.dataConsumer(this)
        } finally {
            ScheduledActionManager.deleteScheduledAction(this.id)
        }
    }

    enum class ActionType(val expectedClass: KClass<*>, val dataConsumer: (ScheduledAction) -> Unit) {
        UNMUTE(ModerationActionData::class, { action -> TODO() }),
        UNBAN(ModerationActionData::class, { action -> TODO() }),
        REMINDER(ReminderActionData::class, { action -> TODO() });

        fun verifyDataType(data: ActionData) = data::class.isSubclassOf(expectedClass)
    }

    interface ActionData

    class ModerationActionData(val targetId: Long) : ActionData {
        // Mongo Constructor
        @Suppress("unused")
        private constructor() : this(0L)
    }

    class ReminderActionData(val reminder: String, val isDM: Boolean = false) : ActionData {
        // Mongo Constructor
        @Suppress("unused")
        private constructor() : this("ugh", false)
    }

}
