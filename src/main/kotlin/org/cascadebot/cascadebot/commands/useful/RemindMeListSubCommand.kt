/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.useful

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.managers.ScheduledActionManager
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.scheduler.ScheduledAction

class RemindMeListSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        val actions = ScheduledActionManager.scheduledActions.keys.filter {
            it.data is ScheduledAction.ReminderActionData &&
                    it.userId == context.user.idLong
        }.sortedByDescending { it.executionTime }
        val dmActions = actions.filter { it.data is ScheduledAction.ReminderActionData && it.data.isDM }
    }

    override fun command(): String = "list"

    override fun parent(): String = "remindme"

    override fun permission(): CascadePermission? = null
}