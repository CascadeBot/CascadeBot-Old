package org.cascadebot.cascadebot.commands.moderation

import javassist.NotFoundException
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

class UnlockCommand : MainCommand() {
    override fun onCommand(sender: Member, context: CommandContext) {
        var channel: TextChannel = context.channel
        if (context.args.isNotEmpty()) {
            channel = DiscordUtils.getTextChannel(context.guild, context.getArg(0))
                    ?: return context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_channel_matching", context.getArg(0)))

        }

        val target: ISnowflake = if (context.args.size == 2) {
            DiscordUtils.getRole(context.getArg(1), context.guild)
                    ?: DiscordUtils.getMember(context.guild, context.getArg(1))
                    ?: return context.typedMessaging.replyDanger(context.i18n("commands.unlock.invalid_argument", context.getArg(1)))
        } else {
            context.guild.publicRole
        }

        var name = ""
        val completed = try {
            when (target) {
                is Role -> {
                    name = target.asMention
                    LockManager.unlock(context.guild, channel, target)
                }
                is Member -> {
                    name = "%s %s".format(context.i18n("arguments.member"), target.asMention)
                    LockManager.unlock(context.guild, channel, target)
                }
                else -> false
            }
        } catch (e: PermissionException) {
            context.uiMessaging.sendBotDiscordPermError(e.permission)
            return
        } catch (e: NotFoundException) {
            context.typedMessaging.replyWarning(context.i18n("commands.unlock.fail", if (target is TextChannel) context.guild.publicRole.asMention else name!!))
            return
        }
        if (completed) {
            context.typedMessaging.replySuccess(context.i18n("commands.unlock.success", channel.name, name))
        } else {
            context.typedMessaging.replyDanger(context.i18n("commands.unlock.failure", channel.name, name))
        }
    }

    override fun command(): String {
        return "unlock"
    }

    override fun permission(): CascadePermission? {
        return CascadePermission.of("unlock", false, Permission.MANAGE_CHANNEL)
    }

    override fun module(): Module {
        return Module.MODERATION
    }

}