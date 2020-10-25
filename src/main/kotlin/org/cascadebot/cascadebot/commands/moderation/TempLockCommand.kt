package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.exceptions.PermissionException
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.managers.LockManager
import org.cascadebot.cascadebot.data.managers.Status
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

class TempLockCommand : MainCommand() {
    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val longDuration = ParserUtils.parseTextTime(context.getArg(0), false)
        if (longDuration <= 0) {
            context.typedMessaging.replyDanger(context.i18n("responses.invalid_duration"))
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

        val target: ISnowflake = if (context.args.size == 3) {
            DiscordUtils.getRole(context.getArg(1), context.guild)
                    ?: DiscordUtils.getMember(context.guild, context.getArg(2))
                    ?: return context.typedMessaging.replyDanger(context.i18n("commands.templock.invalid_argument", context.getArg(2)))
        } else {
            context.channel
        }

        val toAction = ScheduledAction.LockActionData(channel.idLong, Status.NEUTRAL, 0, 0)

        var name: String? = null
        try {
            when (target) {
                is Role -> {
                    toAction.oldPermission = LockManager.getPerm(channel, target).left

                    LockManager.lock(channel, target)

                    toAction.targetRoleID = target.idLong
                    name = "%s %s".format(context.i18n("arguments.role"), target.asMention)
                }
                is Member -> {
                    toAction.oldPermission = LockManager.getPerm(channel, target).left

                    LockManager.lock(channel, target)

                    toAction.targetMemberID = target.idLong
                    name = "%s %s".format(context.i18n("arguments.member"), target.user.asMention)
                }
            }
        } catch (e: PermissionException) {
            context.uiMessaging.sendBotDiscordPermError(e.permission)
            return
        }

        ScheduledActionManager.registerScheduledAction(ScheduledAction(
                ActionType.UNLOCK,
                toAction,
                context.guild.idLong,
                context.channel.idLong,
                context.member.idLong,
                Instant.now(),
                longDuration
        ))


        val textDuration = FormatUtils.formatTime(longDuration, context.locale, true).replace("(0[hm])".toRegex(), "") +
                " (" + context.i18n("words.until") + " " + FormatUtils.formatDateTime(OffsetDateTime.now().plus(longDuration, ChronoUnit.MILLIS), context.locale) + ")"
        context.typedMessaging.replySuccess(if (target is TextChannel) context.i18n("commands.templock.text_success", target.name, textDuration) else name?.let { context.i18n("commands.templock.success", channel.name, it, textDuration) })
    }


    override fun command(): String {
        return "templock"
    }

    override fun permission(): CascadePermission? {
        return CascadePermission.of("templock", false, Permission.MANAGE_CHANNEL)
    }

    override fun module(): Module {
        return Module.MODERATION
    }

}