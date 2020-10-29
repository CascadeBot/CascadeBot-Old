/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.permissions.CascadePermission
import java.lang.reflect.Field

class AutoRoleListSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isNotEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        val roles = context.data.management.autoRoles.map { context.guild.getRoleById(it) ?: it }
        val autoRoles = StringBuilder()
        for (role in roles) {
            when (role) {
                is Role -> autoRoles.append(role.asMention).append(" (${role.id})")
                is Long -> autoRoles.append("<@$role>")
            }
            autoRoles.append("\n")
        }
        val embedBuilder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, context.user, context.locale)
        embedBuilder.setTitle(context.i18n("words.autorole"))
        embedBuilder.setDescription(context.i18n("commands.autorole.autorole_description"))
        embedBuilder.addField(context.i18n("words.roles"), autoRoles.toString(), false)

        context.typedMessaging.replyInfo(embedBuilder)
    }

    override fun command(): String = "list"

    override fun parent(): String = "autorole"

    override fun permission(): CascadePermission? = CascadePermission.of("autorole.list", false)
}