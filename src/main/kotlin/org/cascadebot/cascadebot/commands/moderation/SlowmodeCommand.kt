package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.ParserUtils

class SlowmodeCommand : MainCommand() {
    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        if (!context.selfMember.hasPermission(context.channel, Permission.MANAGE_CHANNEL)) {
            context.uiMessaging.sendBotDiscordPermError(Permission.MANAGE_CHANNEL)
            return
        }
        val duration = ParserUtils.parseTextTime(context.getArg(0), false) // In milliseconds
        if (duration / 1000 > 21600) {  // 6 hour maximum time imposed by API
            context.typedMessaging.replyWarning(context.i18n("commands.slowmode.time_exceeded"))
            return
        }
        if (duration.toInt() == 0) {
            context.typedMessaging.replyDanger(context.i18n("responses.invalid_duration"))
            return
        }
        var channel: TextChannel = context.channel
        if (!context.args.isEmpty()) {
            try {
                channel = DiscordUtils.getTextChannel(context.guild, context.getArg(1))
            } catch (e: IllegalStateException) {
                context.typedMessaging.replyWarning(context.i18n("commands.slowmode.invalid_channel", context.getArg(1)))
                return
            }
        }
        channel.manager.setSlowmode(duration.toInt() / 1000).queue()
        context.typedMessaging.replySuccess(context.i18n("commands.slowmode.success", context.getArg(0), channel.name))
    }

    override fun command(): String {
        return "slowmode"
    }

    override fun subCommands(): Set<SubCommand> {
        return setOf(SlowmodeResetSubCommand())
    }

    override fun permission(): CascadePermission? {
        return CascadePermission.of("slowmode", false, Permission.MANAGE_CHANNEL)
    }

    override fun module(): Module {
        return Module.MODERATION
    }
}