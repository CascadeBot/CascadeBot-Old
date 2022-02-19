/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import net.dv8tion.jda.api.entities.Message
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.data.entities.GuildSettingsManagementEntity
import org.cascadebot.cascadebot.data.entities.GuildSettingsModerationEntity
import org.cascadebot.cascadebot.data.objects.PurgeCriteria
import java.time.OffsetDateTime
import java.util.regex.Pattern

object PurgeUtils {

    private val linkCheck = Pattern.compile("^(?:https?|ftp)://[^\\s/$.?#].[^\\s]*$")

    /**
     * Purge method that cleans messages based on the criteria received,
     * and the amount of messages to clean.
     *
     * @param context [CommandContext] of the command.
     * @param criteria [PurgeCriteria] to filter for.
     * @param amount Amount of messages to clear
     * @param argument Optional argument, made for `TOKEN` and `USER`
     */
    @JvmStatic
    fun purge(context: CommandContext, criteria: PurgeCriteria, amount: Int, argument: String?) {
        val messageList: MutableSet<Message> = HashSet()
        for (message in context.channel.iterableHistory) {
            if (message == context.message) {
                continue
            }

            if (messageList.size == amount) {
                break
            }

            // If the message was created more than 2 weeks ago, we issue a warning
            // and stop looping through messages.
            if (message.timeCreated.isBefore(OffsetDateTime.now().minusWeeks(2))) {
                context.typedMessaging.replyWarning(context.i18n("commands.purge.restriction_time"))
                break
            }

            // If the setting "purgePinnedMessages" is false and the message is pinned, the message is skipped.
            val moderationEntity = context.getDataObject(GuildSettingsModerationEntity::class.java)
                ?: throw UnsupportedOperationException("TODO") //TODO message
            if (!moderationEntity.purgePinned && message.isPinned) {
                continue
            }

            if (messageMatchesCriteria(criteria, message, argument)) {
                messageList.add(message)
            }
        }

        if (messageList.size < 1) {
            context.typedMessaging.replyWarning(context.i18n("commands.purge.failed_clear"))
            return
        }

        if (messageList.size == 1) {
            messageList
                    .first()
                    .delete()
                    .queue({ context.typedMessaging.replySuccess(context.i18n("commands.purge.successfully_done", messageList.size)) })
                            { context.typedMessaging.replyException(context.i18n("responses.failed_to_run_command"), it) }
        } else {
            context.channel.deleteMessages(messageList)
                    .queue({ context.typedMessaging.replySuccess(context.i18n("commands.purge.successfully_done", messageList.size)) })
                    { context.typedMessaging.replyException(context.i18n("responses.failed_to_run_command"), it) }
        }
    }

    private fun messageMatchesCriteria(criteria: PurgeCriteria, message: Message, argument: String?) : Boolean {
        return when (criteria) {
            PurgeCriteria.ATTACHMENT -> message.attachments.isNotEmpty()
            PurgeCriteria.BOT -> message.author.isBot
            PurgeCriteria.LINK -> linkCheck.matcher(message.contentRaw).matches()
            PurgeCriteria.TOKEN -> message.contentRaw.contains(argument!!.toLowerCase())
            PurgeCriteria.USER -> argument!!.split(" ").any { it.contains(message.author.id) }
            PurgeCriteria.ALL -> true
        }
    }
}
