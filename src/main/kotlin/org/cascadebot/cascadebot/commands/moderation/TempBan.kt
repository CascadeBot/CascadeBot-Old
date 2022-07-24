/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.ParserUtils

class TempBan : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size < 2) {
            context.uiMessaging.replyUsage()
            return
        }

        val targetMember = DiscordUtils.getMember(context.guild, context.getArg(0))

        if (targetMember == null) {
            context.typedMessaging.replyDanger(MessagingObjects.getStandardMessageEmbed(context.i18n("responses.cannot_find_user"), context.user, context.locale))
            return
        }

        val duration = ParserUtils.parseTextTime(context.args[1], false)
        if (duration <= 0) {
            context.typedMessaging.replyDanger(context.i18n("responses.invalid_duration"))
            return
        }

        var reason: String? = null
        if (context.args.size > 2) {
            reason = context.getMessage(2)
        }

        CascadeBot.INS.moderationManager.tempBan(
                context,
                targetMember.user,
                sender,
                reason,
                duration
        )
    }

    override fun command(): String = "tempban"

    override fun module(): Module = Module.MODERATION

}