package org.cascadebot.cascadebot.scheduler

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.requests.ErrorResponse
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.toKotlin
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

enum class ActionType(val expectedClass: KClass<*>, val dataConsumer: (ScheduledAction) -> Unit) {
    UNMUTE(ScheduledAction.ModerationActionData::class, { action -> TODO() }),
    UNBAN(ScheduledAction.ModerationActionData::class, { action -> TODO() }),
    REMINDER(ScheduledAction.ReminderActionData::class, ::reminderAction);

    fun verifyDataType(data: ScheduledAction.ActionData) = data::class.isSubclassOf(expectedClass)
}

private fun reminderAction(action: ScheduledAction) {
    if (action.data is ScheduledAction.ReminderActionData) {
        val warningText = if (Duration.between(action.executionTime, Instant.now()).seconds >= 5) {
            val locale = Language.getGuildLocale(action.guildId)
            Language.i18n(locale, "scheduled_actions.reminder_overdue", FormatUtils.formatDateTime(action.executionTime.atOffset(ZoneOffset.UTC), locale))
        } else {
            null
        }
        if (action.data.isDM) {
            action.user?.openPrivateChannel()?.queue { channel ->
                channel.sendMessage(MessageBuilder()
                        .setEmbed(
                            embed(MessageType.INFO) {
                                description = (warningText?.let { "$it\n\n" } ?: "") +
                                        Language.i18n(action.guildId, "scheduled_actions.reminder_text") +
                                        "\n```\n${action.data.reminder}\n```"
                            }.build()
                        )
                        .build()
                ).queue(null) {
                    action.channel?.sendMessage(MessageBuilder()
                            .append(action.user as IMentionable)
                            .setEmbed(embed(MessageType.INFO) {
                                description = Language.i18n(action.guildId, "scheduled_actions.reminder_dm_error") + "\n\n" +
                                        (warningText?.let { "$it\n\n" } ?: "") +
                                        Language.i18n(action.guildId, "scheduled_actions.reminder_text") +
                                        "\n```\n${action.data.reminder}\n```"
                            }.build())
                            .build())?.queue()
                }
            }
        } else {
            action.channel?.sendMessage(MessageBuilder()
                    .append(action.user as IMentionable)
                    .setEmbed(embed(MessageType.INFO) {
                        description = (warningText?.let { "$it\n\n" } ?: "") +
                                Language.i18n(action.guildId, "scheduled_actions.reminder_text") +
                                "\n```\n${action.data.reminder}\n```"
                    }.build())
                    .build()
            )?.queue()
        }
    }
}