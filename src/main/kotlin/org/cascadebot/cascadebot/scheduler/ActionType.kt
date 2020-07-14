package org.cascadebot.cascadebot.scheduler

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

enum class ActionType(val expectedClass: KClass<*>, val dataConsumer: (ScheduledAction) -> Unit) {
    UNMUTE(ScheduledAction.ModerationActionData::class, { action ->
        action.guild?.let { guild ->

        }
    }),
    UNBAN(ScheduledAction.ModerationActionData::class, { action ->
        if (action.data is ScheduledAction.ModerationActionData) {
            action.guild?.unban(action.data.targetId.toString())?.reason("Temp ban: Unbanning user")?.queue()
        }
    }),
    REMINDER(ScheduledAction.ReminderActionData::class, { action -> TODO() });

    fun verifyDataType(data: ScheduledAction.ActionData) = data::class.isSubclassOf(expectedClass)
}