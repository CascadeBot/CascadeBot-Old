package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.exceptions.PermissionException
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.utils.DiscordUtils

class SlowmodeResetSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        var channel: TextChannel = context.channel
        if (context.args.isNotEmpty()) {
            val tempChannel = DiscordUtils.getTextChannel(context.guild, context.getArg(0))
            if (tempChannel != null) {
                channel = tempChannel
            } else {
                context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_channel_matching", context.getArg(0)))
            }
        }
        if (!context.member.hasPermission(channel, Permission.MANAGE_CHANNEL)) {
            context.uiMessaging.sendUserDiscordPermError(Permission.MANAGE_CHANNEL)
            return
        }
        if (!context.selfMember.hasPermission(channel, Permission.MANAGE_CHANNEL)) {
            context.uiMessaging.sendBotDiscordPermError(Permission.MANAGE_CHANNEL)
            return
        }

        // Slowmode 0 = off
        try {
            channel.manager.setSlowmode(0).queue {
                context.typedMessaging.replySuccess(context.i18n("commands.slowmode.reset.reset_success", channel.name))
            }
        } catch (e: PermissionException) {
            context.uiMessaging.sendBotDiscordPermError(e.permission)
        } catch (e: Exception) {
            context.typedMessaging.replyException("Couldn't change channel permissions", e)
        }


    }

    override fun parent(): String = "slowmode"

    override fun command(): String = "reset"

}