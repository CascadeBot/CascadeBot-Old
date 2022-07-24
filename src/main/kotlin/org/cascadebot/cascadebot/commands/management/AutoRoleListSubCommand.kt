/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildAutoRoleEntity
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.utils.listOf

class AutoRoleListSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isNotEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        val roles = context.transaction {
            listOf(GuildAutoRoleEntity::class.java, "guild_id", context.getGuildId())
        }
            ?: throw UnsupportedOperationException("TODO") // TODO message
        val autoRoles = StringBuilder()
        for (role in roles) {
            autoRoles.append("<@${role.roleId}>").append('\n')
        }
        val embedBuilder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, context.user, context.locale)
        embedBuilder.setTitle(context.i18n("words.autorole"))
        embedBuilder.setDescription(context.i18n("commands.autorole.autorole_description"))
        embedBuilder.addField(context.i18n("words.roles"), autoRoles.toString(), false)

        context.typedMessaging.replyInfo(embedBuilder)
    }

    override fun command(): String = "list"

    override fun parent(): String = "autorole"

}