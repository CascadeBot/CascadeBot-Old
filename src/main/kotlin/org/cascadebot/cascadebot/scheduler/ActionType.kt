package org.cascadebot.cascadebot.scheduler

import org.cascadebot.cascadebot.utils.getMutedRole
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

enum class ActionType(val expectedClass: KClass<*>, val dataConsumer: (ScheduledAction) -> Unit) {
    UNMUTE(ScheduledAction.ModerationActionData::class, { action ->
        if (action.data is ScheduledAction.ModerationActionData) {
            action.guild?.let { guild ->
                val member = guild.getMemberById(action.data.targetId)
                if (member != null) {
                    guild.removeRoleFromMember(member, guild.getMutedRole()).reason("Temp mute: Unmuting").queue()
                }
            }
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