/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildSettingsModerationEntity
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.getMutedRole

class MuteRoleSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.typedMessaging.replyInfo(context.i18n(
                    "commands.mute.role.role_info",
                    context.guild.getMutedRole().asMention
            ))
            return
        }

        val newRole = DiscordUtils.getRole(context.getArg(0), context.guild)
        if (newRole == null) {
            context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_role_matching", context.args[0]))
            return
        }

        val moderation = context.getDataObject(GuildSettingsModerationEntity::class.java)
            ?: throw UnsupportedOperationException("TODO") // TODO message
        moderation.muteRoleId = newRole.idLong
        context.saveDataObject(moderation)
        context.typedMessaging.replySuccess(context.i18n("commands.mute.role.set_role", newRole.asMention))
    }

    override fun command(): String = "role"

    override fun parent(): String = "mute"

    override fun permission(): CascadePermission = CascadePermission.of("mute.role", false)

}