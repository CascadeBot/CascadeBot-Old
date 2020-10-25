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
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class LockCommand : MainCommand() {
    override fun onCommand(sender: Member, context: CommandContext) {
        var channel: TextChannel = context.channel
        if (context.args.isNotEmpty()) {
            channel = DiscordUtils.getTextChannel(context.guild, context.getArg(0))
                    ?: return context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_channel_matching", context.getArg(0)))

        }

        val target: ISnowflake = if (context.args.size == 2) {
            DiscordUtils.getRole(context.getArg(1), context.guild)
                    ?: DiscordUtils.getMember(context.guild, context.getArg(1))
                    ?: DiscordUtils.getTextChannel(context.guild, context.getArg(1))
                    ?: return context.typedMessaging.replyDanger(context.i18n("commands.lock.invalid_argument", context.getArg(0)))
        } else {
            context.guild.publicRole
        }

        if (target is TextChannel) channel = target;

        val name: String = try {
            when (target) {
                is Role -> {
                    LockManager.lock(channel, target)
                    "%s %s".format(context.i18n("arguments.role"), target.asMention)
                }
                is Member -> {
                    LockManager.lock(channel, target)
                    "%s %s".format(context.i18n("arguments.member"), target.asMention)
                }
                else -> ""
            }
        } catch (e: PermissionException) {
            context.uiMessaging.sendBotDiscordPermError(e.permission)
            return
        }

        context.typedMessaging.replySuccess((context.i18n("commands.lock.success", channel.name, name)))
    }


    override fun command(): String {
        return "lock"
    }

    override fun permission(): CascadePermission? {
        return CascadePermission.of("lock", false, Permission.MANAGE_CHANNEL)
    }

    override fun module(): Module {
        return Module.MODERATION
    }

}