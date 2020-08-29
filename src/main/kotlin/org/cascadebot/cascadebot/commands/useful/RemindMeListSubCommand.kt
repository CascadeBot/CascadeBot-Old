/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.useful

import com.ibm.icu.text.DateFormat
import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.managers.ScheduledActionManager
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.scheduler.ScheduledAction
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

class RemindMeListSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        val actions = ScheduledActionManager.scheduledActions.keys.filter {
            it.data is ScheduledAction.ReminderActionData &&
                    it.userId == context.user.idLong
        }.sortedByDescending { it.executionTime }

        val pages: MutableList<Page> = mutableListOf()

        pages.add(PageObjects.EmbedPage(embed(MessageType.INFO, context.user) {
            title {
                name = context.i18n("commands.remindme.list.your_reminders")
            }
            val thisChannelCount = actions.count { it.channelId == context.channel.idLong && it.data is ScheduledAction.ReminderActionData && !it.data.isDM}
            val dmCount = actions.count { it.data is ScheduledAction.ReminderActionData && it.data.isDM}
            description = context.i18n("commands.remindme.list.summary_page", thisChannelCount, context.channel.asMention, dmCount, actions.size)
        }, false))

        for (action in actions) {
            require(action.data is ScheduledAction.ReminderActionData)

            val absoluteDateTime = FormatUtils.formatDateTime(OffsetDateTime.now().plus(Duration.ofMillis(action.delay)), DateFormat.SHORT, DateFormat.LONG, context.locale)
            pages.add(PageObjects.EmbedPage(embed(MessageType.INFO, context.user) {
                title {
                    name = context.i18n("words.reminder").capitalize()
                }
                description = action.data.reminder
                field {
                    name = context.i18n("words.location").capitalize()
                    value = if (action.data.isDM) {
                        context.i18n("words.dm")
                    } else {
                        action.channel?.asMention
                    }
                }
                field {
                    name = context.i18n("words.created_at").capitalize()
                    value = FormatUtils.formatDateTime(action.creationTime.atOffset(ZoneOffset.UTC), DateFormat.SHORT, DateFormat.LONG, context.locale)
                    inline = true
                }
                field {
                    name = context.i18n("commands.remindme.list.remind_datetime")
                    value = absoluteDateTime
                    inline = true
                }
            }, false))
        }

        context.uiMessaging.sendPagedMessage(pages)

    }

    override fun command(): String = "list"

    override fun parent(): String = "remindme"

    override fun permission(): CascadePermission? = null
}