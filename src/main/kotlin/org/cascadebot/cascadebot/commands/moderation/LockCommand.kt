package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.IPermissionHolder
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
import java.lang.IllegalStateException

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
                    ?: return context.typedMessaging.replyDanger(context.i18n("commands.lock.invalid_argument", context.getArg(0)))
        } else {
            context.guild.publicRole
        }

        if (target !is IPermissionHolder) error("Target must be a IPermissionHolder")

        val success = {
            val name: String = when(target) {
                is Role -> target.asMention
                is Member -> target.asMention
                else -> error("Target must be either a Role or a Member")
            }

            if (target is Member) {
                context.typedMessaging.replySuccess((context.i18n("commands.lock.success_member", channel.name, target.asMention)))
            } else if (target is Role) {
                context.typedMessaging.replySuccess((context.i18n("commands.lock.success_role", channel.name, target.asMention)))
            }

        }

        val failure = { throwable: Throwable ->
            if (throwable is PermissionException) {
                context.uiMessaging.sendBotDiscordPermError(throwable.permission)
            } else {
                context.typedMessaging.replyException("Something went wrong!", throwable)
            }
        }

        LockManager.lock(channel, target, success, failure)
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