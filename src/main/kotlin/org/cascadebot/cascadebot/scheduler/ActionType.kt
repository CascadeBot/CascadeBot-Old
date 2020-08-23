package org.cascadebot.cascadebot.scheduler

import net.dv8tion.jda.api.exceptions.PermissionException
import org.cascadebot.cascadebot.data.language.Language.i18n
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.Messaging
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

enum class ActionType(val expectedClass: KClass<*>, val dataConsumer: (ScheduledAction) -> Unit) {
    UNMUTE(ScheduledAction.ModerationActionData::class, { action -> TODO() }),
    UNBAN(ScheduledAction.ModerationActionData::class, { action -> TODO() }),
    REMINDER(ScheduledAction.ReminderActionData::class, { action -> TODO() }),

    UNSLOWMODE(ScheduledAction.SlowmodeActionData::class, { action ->
        if (action.data is ScheduledAction.SlowmodeActionData) {
            action.guild?.let { guild ->
                val targetChannel = guild.getGuildChannelById(action.data.targetId)
                targetChannel?.manager?.setSlowmode(action.data.oldSlowmode)?.queue(null, {
                    if (it is PermissionException) {
                        action.channel?.let { channel -> Messaging.sendMessage(MessageType.DANGER, channel, i18n(action.guildId, "responses.no_discord_perm_bot", it.permission)) }
                    } else {
                        action.channel?.let { channel -> Messaging.sendExceptionMessage(channel, "Couldn't unslowmode %s".format(targetChannel), it) }
                    }
                })
            }
        }
    });

    fun verifyDataType(data: ScheduledAction.ActionData) = data::class.isSubclassOf(expectedClass)
}