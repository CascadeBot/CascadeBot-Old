/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.permissions.CascadePermission

class AutoRoleCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        context.uiMessaging.replyUsage()
    }

    override fun command(): String = "autorole"

    override fun subCommands(): Set<SubCommand> = setOf(AutoRoleAddSubCommand(), AutoRoleRemoveSubCommand(), AutoRoleListSubCommand())

    override fun module(): Module = Module.MANAGEMENT

    override fun permission(): CascadePermission? = null

}