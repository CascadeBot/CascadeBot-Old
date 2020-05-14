/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.ISubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.shared.Regex

class AutoRoleRemoveSubCommand : ISubCommand {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        val roles: MutableList<Role> = mutableListOf()
        val errorInputs: MutableList<String> = mutableListOf()
        for (arg in context.args) {
            val role = DiscordUtils.getRole(arg, context.guild)
            if (role != null) {
                roles.add(role)
            } else {
                errorInputs.add(arg)
            }
        }

        context.data.management.autoRoles.removeAll(roles.map { it.idLong })

        if (roles.isEmpty()) {
            if (errorInputs.isNotEmpty()) {
                context.typedMessaging.replyDanger("Could not parse any of arguments to a role! Please enter role IDs or role mentions!\n" +
                        "Inputs that could not be parsed: ${errorInputs.joinToString(", ") { "`$it`" }}")
            } else {
                context.typedMessaging.replyDanger("This shouldn't happen...! Something went wrong!")
            }
        } else {
            if (errorInputs.isEmpty()) {
                context.typedMessaging.replySuccess("Successfully removed all of the roles from AutoRole!\n" +
                        "Removed roles: ${roles.joinToString(" "){ it.asMention }}")
            } else {
                context.typedMessaging.replyWarning("Successfully removed some of the roles from AutoRole!\n" +
                        "Removed roles: ${roles.joinToString(" "){ it.asMention }}\n" +
                        "Inputs that could not be parsed: ${errorInputs.joinToString(", ") { "`$it`" }}")
            }
        }

    }

    override fun parent(): String = "autorole"

    override fun command(): String = "add"

    override fun getPermission(): CascadePermission? = null

}