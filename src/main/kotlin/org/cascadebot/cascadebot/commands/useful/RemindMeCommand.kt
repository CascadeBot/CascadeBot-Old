/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.useful

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
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class RemindMeCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.testForArg("dm")) {

        } else {
            val duration = ParserUtils.parseTextTime(context.getArg(0), true)
            val message = context.getMessage(1)
            ScheduledActionManager.registerScheduledAction(
                    ScheduledAction(
                            ActionType.REMINDER,
                            ScheduledAction.ReminderActionData(message),
                            context.guild.idLong,
                            context.channel.idLong,
                            context.user.idLong,
                            Instant.now(),
                            duration
                    )
            )
            context.typedMessaging.replySuccess("Reminder created for " + FormatUtils.formatDateTime(OffsetDateTime.now().plus(duration, ChronoUnit.MILLIS), context.locale))      }
    }

    override fun command(): String = "remindme"

    override fun module(): Module = Module.USEFUL

    override fun permission(): CascadePermission = CascadePermission.of("remindme", true)

}