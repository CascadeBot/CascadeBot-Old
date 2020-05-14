/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.ICommandMain
import org.cascadebot.cascadebot.commandmeta.ISubCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.permissions.CascadePermission

class AutoRoleCommand : ICommandMain {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isNotEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        val roles = context.data.management.autoRoles.map { context.guild.getRoleById(it) ?: it }
        val autoRoles = StringBuilder()
        for (role in roles) {
            when (role) {
                is Role -> autoRoles.append(role.asMention).append(" (${role.id})").append(" ")
                is Long -> autoRoles.append("<@$role>").append(" ")
            }
        }
        val embedBuilder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, context.user)
        embedBuilder.setTitle("AutoRole")
        embedBuilder.setDescription("""
            The roles listed below will automatically be assigned to users when they join.
            
            Roles: $autoRoles
        """.trimIndent())

        context.typedMessaging.replyInfo(embedBuilder)
    }

    override fun command(): String = "autorole"

    override fun getSubCommands(): Set<ISubCommand> = setOf(AutoRoleAddSubCommand())

    override fun getModule(): Module = Module.MANAGEMENT

    override fun getPermission(): CascadePermission = CascadePermission.of("autorole", false)

}