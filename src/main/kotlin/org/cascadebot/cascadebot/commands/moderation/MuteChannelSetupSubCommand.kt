/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildSettingsCoreEntity
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.Messaging
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.ConfirmUtils
import org.cascadebot.cascadebot.utils.getMutedRole
import java.util.concurrent.CompletableFuture

class MuteChannelSetupSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (!context.selfMember.hasPermission(Permission.MANAGE_CHANNEL)) {
            context.uiMessaging.sendBotDiscordPermError(Permission.MANAGE_CHANNEL)
            return
        } else if (!context.selfMember.hasPermission(Permission.MANAGE_PERMISSIONS)) {
            context.uiMessaging.sendBotDiscordPermError(Permission.MANAGE_PERMISSIONS)
            return
        }

        if (ConfirmUtils.hasRegisteredAction("mute.channelsetup", context.user.idLong)) {
            ConfirmUtils.confirmAction("mute.channelsetup", context.user.idLong)
            return
        }

        ConfirmUtils.registerForConfirmation(
                context.user.idLong,
                "mute.channelsetup",
                context.channel,
                MessageType.WARNING,
                context.i18n("commands.mute.channelsetup.warning"),
                isCancellable = true,
                action = Runnable {
                    channelSetup(context)
                }
        )
    }

    override fun command(): String = "channelsetup"

    override fun parent(): String = "mute"

    override fun permission(): CascadePermission = CascadePermission.of("mute.channelsetup", false)

    private fun channelSetup(context: CommandContext) {
        val mutedRole = context.guild.getMutedRole()
        val success = mutableListOf<GuildChannel>()
        val failed = mutableListOf<GuildChannel>()

        val futures: MutableList<CompletableFuture<Void>> = mutableListOf()

        context.guild.channels.forEach {
            try {
                futures.add(when (it) {
                    is TextChannel -> it.manager.putPermissionOverride(mutedRole, setOf(), setOf(Permission.MESSAGE_WRITE)).submit()
                    is VoiceChannel -> it.manager.putPermissionOverride(mutedRole, setOf(), setOf(Permission.VOICE_SPEAK)).submit()
                    is Category -> {
                        it.manager.putPermissionOverride(mutedRole, setOf(), setOf(Permission.MESSAGE_WRITE, Permission.VOICE_SPEAK)).submit()
                    }

                    else -> return@forEach
                })
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

        CompletableFuture.allOf(*futures.toTypedArray()).whenComplete { _, _ ->
            val messageType = when {
                success.isNotEmpty() && failed.isNotEmpty() -> MessageType.WARNING
                success.isNotEmpty() -> MessageType.SUCCESS
                failed.isNotEmpty() -> MessageType.DANGER
                else -> throw IllegalStateException("Logically this should not happen!")
            }

            val successText = if (success.isNotEmpty()) {
                context.i18n("commands.mute.channelsetup.perm_success") + "\n" +
                        success.joinToString("\n") { "- ${if (it is TextChannel) it.asMention else it.name}" }
            } else ""
            val failureText = if (failed.isNotEmpty()) {
                context.i18n("commands.mute.channelsetup.perm_failure") + "\n" +
                        failed.joinToString("\n") { "- ${if (it is TextChannel) it.asMention else it.name}" } + "\n\n" +
                        context.i18n("commands.mute.channelsetup.perm_failure_footer")
            } else ""

            Messaging.sendEmbedMessage(
                    messageType,
                    context.channel,
                    EmbedBuilder()
                            .setTitle(context.i18n("commands.mute.channelsetup.embed_title"))
                            .setDescription("$successText\n\n$failureText".trim()),
                    context.getDataObject(GuildSettingsCoreEntity::class.java)!!.useEmbeds
            )
        }
    }

}