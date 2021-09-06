/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class AutoRoleRemoveSubCommand : DeprecatedSubCommand() {

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
            check(errorInputs.isNotEmpty()) { "Error inputs should contain data if no roles have been successfully parsed!" }
            context.typedMessaging.replyDanger(context.i18n("commands.autorole.parse_failed") + "\n" +
                    context.i18n("commands.autorole.inputs_unparsed") + " ${errorInputs.joinToString(", ") { "`$it`" }}")
        } else {
            if (errorInputs.isEmpty()) {
                context.typedMessaging.replySuccess(context.i18n("commands.autorole.remove.remove_success") + "\n" +
                        context.i18n("commands.autorole.remove.removed_roles") + " ${roles.joinToString(" ") { it.asMention }}")
            } else {
                context.typedMessaging.replyWarning(context.i18n("commands.autorole.remove.remove_success") + "\n" +
                        context.i18n("commands.autorole.remove.removed_roles") + " ${roles.joinToString(" ") { it.asMention }}\n" +
                        context.i18n("commands.autorole.inputs_unparsed") + " ${errorInputs.joinToString(", ") { "`$it`" }}")
            }
        }

    }

    override fun parent(): String = "autorole"

    override fun command(): String = "remove"

    override fun permission(): CascadePermission = CascadePermission.of("autorole.remove", false)

}