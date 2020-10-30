/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class AutoRoleAddSubCommand : SubCommand() {

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

        // TODO Shouldn't be allowed to add roles higher than themself

        context.data.management.autoRoles.addAll(roles.map { it.idLong })

        if (roles.isEmpty()) {
            check(errorInputs.isNotEmpty()) { "Error inputs should contain data if no roles have been successfully parsed!" }
            context.typedMessaging.replyDanger(context.i18n("commands.autorole.parse_failed") + "\n" +
                    context.i18n("commands.autorole.inputs_unparsed") + " ${errorInputs.joinToString(", ") { "`$it`" }}")
        } else {
            if (errorInputs.isEmpty()) {
                context.typedMessaging.replySuccess(context.i18n("commands.autorole.add.add_success") + "\n" +
                        context.i18n("commands.autorole.add.added_roles") + " ${roles.joinToString(" ") { it.asMention }}")
            } else {
                context.typedMessaging.replyWarning(context.i18n("commands.autorole.add.add_success") + "\n" +
                        context.i18n("commands.autorole.add.added_roles") + " ${roles.joinToString(" ") { it.asMention }}\n" +
                        context.i18n("commands.autorole.inputs_unparsed") + " ${errorInputs.joinToString(", ") { "`$it`" }}")
            }
        }

    }

    override fun parent(): String = "autorole"

    override fun command(): String = "add"

    override fun permission(): CascadePermission = CascadePermission.of("autorole.add", false)

}