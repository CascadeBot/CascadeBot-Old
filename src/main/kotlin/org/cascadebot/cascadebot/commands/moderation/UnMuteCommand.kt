/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.exceptions.PermissionException
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.managers.ScheduledActionManager
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.moderation.ModerationManager
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.scheduler.ActionType
import org.cascadebot.cascadebot.scheduler.ScheduledAction
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.getMutedRole

class UnMuteCommand : MainCommand() {

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

        val mutedRole = context.guild.getMutedRole()

        if (targetMember.roles.contains(mutedRole)) {
            ScheduledActionManager.removeIf {
                if (it.type != ActionType.UNMUTE) return@removeIf false
                if (it.data !is ScheduledAction.ModerationActionData) return@removeIf false
                it.data.targetId == targetMember.idLong
            }
            context.guild.removeRoleFromMember(targetMember, mutedRole)
                    .reason(context.i18n("mod_actions.unmute.reason", targetMember.user.asTag, context.user.asTag))
                    .queue(null) {
                        if (it is PermissionException) {
                            context.uiMessaging.sendBotDiscordPermError(it.permission)
                        } else {
                            context.typedMessaging.replyException("Couldn't unmute the user!", it)
                        }
                    }
        } else {
            context.typedMessaging.replyWarning(context.i18n("commands.unmute.not_muted", targetMember.asMention))
        }


    }

    override fun command(): String = "unmute"

    override fun module(): Module = Module.MODERATION

    override fun permission(): CascadePermission = CascadePermission.of("unmute", false, Permission.MANAGE_ROLES)

}