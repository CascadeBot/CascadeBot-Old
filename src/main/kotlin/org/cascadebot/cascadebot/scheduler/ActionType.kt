package org.cascadebot.cascadebot.scheduler

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

enum class ActionType(val expectedClass: KClass<*>, val dataConsumer: (ScheduledAction) -> Unit) {
    UNMUTE(ScheduledAction.ModerationActionData::class, { action -> TODO() }),
    UNBAN(ScheduledAction.ModerationActionData::class, { action -> TODO() }),
    REMINDER(ScheduledAction.ReminderActionData::class, { action -> TODO() });

    fun verifyDataType(data: ScheduledAction.ActionData) = data::class.isSubclassOf(expectedClass)
}