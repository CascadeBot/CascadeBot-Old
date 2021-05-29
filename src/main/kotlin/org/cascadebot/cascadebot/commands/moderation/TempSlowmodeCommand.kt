package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.exceptions.PermissionException
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.managers.ScheduledActionManager
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.scheduler.ActionType
import org.cascadebot.cascadebot.scheduler.ScheduledAction
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.ParserUtils
import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

class TempSlowmodeCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size < 2) {
            context.uiMessaging.replyUsage()
            return
        }

        val longInterval = ParserUtils.parseTextTime(context.getArg(0), false) // In milliseconds
        if (longInterval / 1000 > 21600) {  // 6 hour maximum time imposed by API
            context.typedMessaging.replyWarning(context.i18n("commands.slowmode.time_exceeded"))
            return
        }

        val longDuration = ParserUtils.parseTextTime(context.getArg(1), false)
        if (longDuration <= 0) {
            context.typedMessaging.replyDanger(context.i18n("responses.invalid_duration"))
            return
        }

        var channel: TextChannel = context.channel
        if (context.args.size == 3) {
            val tempChannel = DiscordUtils.getTextChannel(context.guild, context.getArg(2))
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

        val oldSlowmode = channel.slowmode
        try {
            channel.manager.setSlowmode(longInterval.toInt() / 1000).queue {
                val textInterval = FormatUtils.formatDuration(longInterval, context.locale, true).replace("(0[hm])".toRegex(), "")
                ScheduledActionManager.registerScheduledAction(ScheduledAction(
                        ActionType.UNSLOWMODE,
                        ScheduledAction.SlowmodeActionData(channel.idLong, oldSlowmode),
                        context.guild.idLong,
                        context.channel.idLong,
                        context.member.idLong,
                        Instant.now(),
                        longDuration
                ))
                val textDuration = FormatUtils.formatDuration(longDuration, context.locale, true).replace("(0[hm])".toRegex(), "") +
                        " (" + context.i18n("words.until") + " " + FormatUtils.formatDateTime(OffsetDateTime.now().plus(longDuration, ChronoUnit.SECONDS), context.locale) + ")"
                context.typedMessaging.replySuccess(context.i18n("commands.tempslowmode.success", textInterval, channel.name, textDuration))
            }
        } catch (e: PermissionException) {
            context.uiMessaging.sendBotDiscordPermError(e.permission)
        } catch (e: Exception) {
            context.typedMessaging.replyException("Couldn't change channel permissions", e)
        }

    }


    override fun command(): String {
        return "tempslowmode"
    }

    override fun permission(): CascadePermission? {
        return CascadePermission.of("tempslowmode", false, Permission.MANAGE_CHANNEL)
    }

    override fun module(): Module {
        return Module.MODERATION
    }

}