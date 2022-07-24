package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.exceptions.PermissionException
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.ParserUtils

class SlowmodeCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        var channel: TextChannel = context.channel
        if (context.args.size == 2) {
            val tempChannel = DiscordUtils.getTextChannel(context.guild, context.getArg(1))
            if (tempChannel != null) {
                channel = tempChannel
            } else {
                context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_channel_matching", context.getArg(1)))
                return
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

        val duration = ParserUtils.parseTextTime(context.getArg(0), false) // In milliseconds
        if (duration / 1000 > 21600) {  // 6 hour maximum time imposed by API
            context.typedMessaging.replyWarning(context.i18n("commands.slowmode.time_exceeded"))
            return
        }
        if (duration == 0L) {
            context.typedMessaging.replyDanger(context.i18n("responses.invalid_duration"))
            return
        }
        try {
            channel.manager.setSlowmode(duration.toInt() / 1000).queue {
                val interval: String = FormatUtils.formatTime(duration, context.locale, true).replace("(0[hms])".toRegex(), "")
                context.typedMessaging.replySuccess(context.i18n("commands.slowmode.success", interval, channel.name))
            }

        } catch (e: PermissionException) {
            context.uiMessaging.sendBotDiscordPermError(e.permission)
        } catch (e: Exception) {
            context.typedMessaging.replyException("Couldn't change channel permissions", e)
        }

    }

    override fun command(): String {
        return "slowmode"
    }

    override fun subCommands(): Set<SubCommand> {
        return setOf(SlowmodeResetSubCommand())
    }

    override fun module(): Module {
        return Module.MODERATION
    }
}