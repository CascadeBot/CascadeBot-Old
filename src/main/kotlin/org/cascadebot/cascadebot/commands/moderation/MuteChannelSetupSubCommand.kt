/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.Messaging
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.getMutedRole

class MuteChannelSetupSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        val mutedRole = context.guild.getMutedRole()
        val success = mutableListOf<GuildChannel>()
        val failed = mutableListOf<GuildChannel>()
        context.guild.channels.forEach {
            try {
                when (it) {
                    is TextChannel -> it.manager.putPermissionOverride(mutedRole, 0, Permission.MESSAGE_WRITE.rawValue).complete()
                    is Category -> {
                        // Don't modify voice only categories
                        if (it.textChannels.isEmpty()) return@forEach
                        it.manager.putPermissionOverride(mutedRole, 0, Permission.MESSAGE_WRITE.rawValue).complete()
                    }
                    else -> return@forEach
                }
                success.add(it)
            } catch (e: InsufficientPermissionException) {
                if (e.permission == Permission.MANAGE_PERMISSIONS ||
                        e.permission == Permission.MANAGE_CHANNEL) {
                    failed.add(it)
                } else {
                    throw e
                }
            }
        }
        val messageType = when {
            success.isNotEmpty() && failed.isNotEmpty() -> MessageType.WARNING
            success.isNotEmpty() -> MessageType.SUCCESS
            failed.isNotEmpty() -> MessageType.DANGER
            else -> throw IllegalStateException("Logically this should not happen!")
        }

        val successText = if (success.isNotEmpty()) {
            """${context.i18n("commands.mute.channelsetup.perm_success")}
               ${success.joinToString("\n") { "- ${if (it is TextChannel) it.asMention else it.name}" }}
            """.trimIndent()
        } else ""
        val failureText = if (failed.isNotEmpty()) {
            """${context.i18n("commands.mute.channelsetup.perm_failure")}
               ${failed.joinToString("\n") { "- ${if (it is TextChannel) it.asMention else it.name}" }}
               
               ${context.i18n("commands.mute.channelsetup.perm_failure_footer")}""".trimIndent()
        } else ""

        Messaging.sendEmbedMessage(
                messageType,
                context.channel,
                EmbedBuilder()
                        .setTitle(context.i18n("commands.mute.channelsetup.embed_title"))
                        .setDescription("$successText\n\n$failureText".trim()),
                context.data.core.useEmbedForMessages
        )
    }

    override fun command(): String = "channelsetup"

    override fun parent(): String = "mute"

    override fun permission(): CascadePermission = CascadePermission.of("mute.channelsetup", false)

}