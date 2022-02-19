/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildAutoRoleEntity
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.Messaging
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

        var highest = 0;
        for (role in context.member.roles) {
            if (role.position > highest) {
                highest = role.position
            }
        }

        val failedRoles: MutableSet<Role> = mutableSetOf()
        context.transactionNoReturn {
            for (role in roles) {
                if (role.position < highest) {
                    val autoRole: GuildAutoRoleEntity = GuildAutoRoleEntity(context.getGuildId(), role.idLong)
                    save(autoRole)
                } else {
                    failedRoles.add(role)
                }
            }
        }

        val resultBuilder = StringBuilder()
        var resultType: MessageType
        if (!roles.isEmpty()) {
            resultType = MessageType.SUCCESS
            resultBuilder.append(context.i18n("commands.autorole.add.add_success"))
            resultBuilder.append(context.i18n("commands.autorole.add.added_roles")).append(" ${roles.joinToString(" ") { it.asMention }}")
        } else {
            resultBuilder.append(context.i18n("commands.autorole.add.add_failed"))
            resultType = MessageType.DANGER
        }
        if (!errorInputs.isEmpty()) {
            resultBuilder.append('\n').append(context.i18n("commands.autorole.inputs_unparsed")).append(" ${errorInputs.joinToString(", ") { "`$it`" }}")
            if (!resultType.equals(MessageType.DANGER)) {
                resultType = MessageType.WARNING
            }
        }
        if (!failedRoles.isEmpty()) {
            resultBuilder.append('\n').append(context.i18n("commands.autorole.add.higher_roles")).append(" ${failedRoles.joinToString(" ") { it.asMention }}")
            if (!resultType.equals(MessageType.DANGER)) {
                resultType = MessageType.WARNING
            }
        }
        Messaging.sendMessage(resultType, context.channel, resultBuilder.toString(), true)

    }

    override fun parent(): String = "autorole"

    override fun command(): String = "add"

    override fun permission(): CascadePermission = CascadePermission.of("autorole.add", false)

}