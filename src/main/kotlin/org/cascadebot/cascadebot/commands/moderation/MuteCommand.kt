/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class MuteCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val targetMember = DiscordUtils.getMember(context.guild, context.getArg(0))

        if (targetMember == null) {
            context.typedMessaging.replyDanger(MessagingObjects.getStandardMessageEmbed(context.i18n("responses.cannot_find_user"), context.user))
            return
        }

        var reason: String? = null
        if (context.args.size > 1) {
            reason = context.getMessage(1)
        }

        CascadeBot.INS.moderationManager.mute(
                context,
                targetMember,
                sender,
                reason
        )
    }

    override fun command(): String = "mute"

    override fun module(): Module = Module.MODERATION

    override fun subCommands(): Set<SubCommand> = setOf(MuteRoleSubCommand(), MuteChannelSetupSubCommand())

    override fun permission(): CascadePermission = CascadePermission.of("mute", false, Permission.MANAGE_ROLES)
}