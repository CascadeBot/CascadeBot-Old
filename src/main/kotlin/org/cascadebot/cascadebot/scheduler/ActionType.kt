package org.cascadebot.cascadebot.scheduler

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.IMentionable
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.Messaging
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.getMutedRole
import org.cascadebot.cascadebot.utils.toCapitalized
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

enum class ActionType(val expectedClass: KClass<*>, val dataConsumer: (ScheduledAction) -> Unit) {
    REMINDER(ScheduledAction.ReminderActionData::class, ::reminderAction),
    UNMUTE(ScheduledAction.ModerationActionData::class, { action ->
        if (action.data is ScheduledAction.ModerationActionData) {
            action.guild?.let { guild ->
                val member = guild.getMemberById(action.data.targetId)
                if (member != null) {
                    guild.removeRoleFromMember(member, guild.getMutedRole()).apply {
                        val userName = guild.getMemberById(action.data.targetId)?.user?.asTag ?: Language.getGuildLocale(guild.idLong).i18n("words.unknown").toCapitalized()
                        reason(Language.getGuildLocale(guild.idLong).i18n("mod_actions.temp_mute.unmute_reason", userName))
                        queue(null) {
                            action.channel?.let channelLet@{ channel ->
                                if (it is net.dv8tion.jda.api.exceptions.PermissionException) {
                                    // TODO: Log something to modlog
                                    return@channelLet
                                }
                                Messaging.sendExceptionMessage(channel, "Failed to unmute user!", it)
                            }
                        }
                    }
                }
            }
        }
    }),
    UNBAN(ScheduledAction.ModerationActionData::class, { action ->
        if (action.data is ScheduledAction.ModerationActionData) {
            action.guild?.let { guild ->
                guild.unban(action.data.targetId.toString()).apply {
                    val userName = org.cascadebot.cascadebot.CascadeBot.INS.shardManager.getUserById(action.data.targetId)
                            ?: Language.getGuildLocale(guild.idLong).i18n("words.unknown").toCapitalized()
                    reason(Language.getGuildLocale(guild.idLong).i18n("mod_actions.temp_mute.unmute_reason", userName))
                    queue(null) {
                        action.channel?.let channelLet@{ channel ->
                            if (it is net.dv8tion.jda.api.exceptions.PermissionException) {
                                // TODO: Log something to modlog
                                return@channelLet
                            }
                            Messaging.sendExceptionMessage(channel, "Failed to unban user!", it)
                        }
                    }
                }
            }
        }
    })

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