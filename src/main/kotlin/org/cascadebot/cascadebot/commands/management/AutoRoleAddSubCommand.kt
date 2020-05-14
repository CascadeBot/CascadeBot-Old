/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.ISubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class AutoRoleAddSubCommand : ISubCommand {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        val roles: MutableSet<Role> = mutableSetOf()
        val errorInputs: MutableList<String> = mutableListOf()
        for (arg in context.args) {
            val role = DiscordUtils.getRole(arg, context.guild)
            if (role != null) {
                roles.add(role)
            } else {
                errorInputs.add(arg)
            }
        }

        context.data.management.autoRoles.addAll(roles.map { it.idLong })

        if (roles.isEmpty()) {
            require(errorInputs.isNotEmpty()) { "Error inputs should contain data if no roles have been successfully parsed!" }
            context.typedMessaging.replyDanger("Could not parse any of arguments to a role! Please enter role IDs or role mentions!\n" +
                    "Inputs that could not be parsed: ${errorInputs.joinToString(", ") { "`$it`" }}")
        } else {
            if (errorInputs.isEmpty()) {
                context.typedMessaging.replySuccess("Successfully added all of the roles to AutoRole!\n" +
                        "Added roles: ${roles.joinToString(" ") { it.asMention }}")
            } else {
                context.typedMessaging.replyWarning("Successfully added some of the roles to AutoRole!\n" +
                        "Added roles: ${roles.joinToString(" ") { it.asMention }}\n" +
                        "Inputs that could not be parsed: ${errorInputs.joinToString(", ") { "`$it`" }}")
            }
        }

    }

    override fun parent(): String = "autorole"

    override fun command(): String = "add"

    override fun getPermission(): CascadePermission? = null

}