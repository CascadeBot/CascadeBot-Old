/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.useful

import com.ibm.icu.text.DateFormat
import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.managers.ScheduledActionManager
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.scheduler.ActionType
import org.cascadebot.cascadebot.scheduler.ScheduledAction
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.ParserUtils
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime

class RemindMeCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        val dm = context.testForArg("dm")
        val delay = ParserUtils.parseTextTime(context.getArg(if (dm) 1 else 0), false)
        if (delay == 0L) {
            context.typedMessaging.replyDanger("")
        }
        val message = context.getMessage(if (dm) 2 else 1)
        ScheduledActionManager.registerScheduledAction(
                ScheduledAction(
                        ActionType.REMINDER,
                        ScheduledAction.ReminderActionData(message, dm),
                        context.guild.idLong,
                        context.channel.idLong,
                        context.user.idLong,
                        Instant.now(),
                        delay
                )
        )
        val duration = Duration.ofMillis(delay)
        if (duration.toDays() < 1) {
            val relativeDuration = FormatUtils.formatRelativeDuration(duration, context.locale)
            val absoluteTime = FormatUtils.formatTime(OffsetDateTime.now().plus(duration).toOffsetTime(), DateFormat.LONG, context.locale)
            context.typedMessaging.replySuccess("I will remind you ${if (dm) "in your DMs" else ""} $relativeDuration at $absoluteTime")
        } else {
            val absoluteDateTime = FormatUtils.formatDateTime(OffsetDateTime.now().plus(duration), DateFormat.SHORT, DateFormat.LONG, context.locale)
            context.typedMessaging.replySuccess("I will remind you ${if (dm) "in your DMs" else ""} at $absoluteDateTime")
        }
//            context.typedMessaging.replySuccess("Reminder created for " + FormatUtils.formatDateTime(, context.locale))
    }

    override fun command(): String = "remindme"

    override fun module(): Module = Module.USEFUL

    override fun permission(): CascadePermission = CascadePermission.of("remindme", true)

}