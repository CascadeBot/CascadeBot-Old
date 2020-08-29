package org.cascadebot.cascadebot.scheduler

import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.messaging.Messaging
import org.cascadebot.cascadebot.utils.getMutedRole
import org.cascadebot.cascadebot.utils.toCapitalized
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

enum class ActionType(val expectedClass: KClass<*>, val dataConsumer: (ScheduledAction) -> Unit) {
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
    }),
    REMINDER(ScheduledAction.ReminderActionData::class, { action -> TODO() });

    fun verifyDataType(data: ScheduledAction.ActionData) = data::class.isSubclassOf(expectedClass)
}