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

class UnlockCommand : MainCommand() {
    override fun onCommand(sender: Member, context: CommandContext) {
        var channel: TextChannel = context.channel
        if (context.args.size == 2) {
            channel = DiscordUtils.getTextChannel(context.guild, context.getArg(1))
                    ?: return context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_channel_matching", context.getArg(1)))

        }

        val temp: ISnowflake? = if (context.args.isNotEmpty()) {
            DiscordUtils.getRole(context.getArg(0), context.guild)
                    ?: DiscordUtils.getMember(context.guild, context.getArg(0))
                    ?: DiscordUtils.getTextChannel(context.guild, context.getArg(0))
        } else {
            context.channel
        }

        var name: String? = null
        try {
            when (temp) {
                is Role -> {
                    name = "%s %s".format(context.i18n("arguments.role"), temp.asMention)
                    LockManager.unlock(context.guild, channel, temp)
                }
                is Member -> {
                    name = "%s %s".format(context.i18n("arguments.member"), temp.asMention)
                    LockManager.unlock(context.guild, channel, temp)
                }
                is TextChannel -> LockManager.unlock(context.guild, temp, context.guild.publicRole)
            }
        } catch (e: PermissionException) {
            context.uiMessaging.sendBotDiscordPermError(e.permission)
            return
        }
        context.typedMessaging.replySuccess(if (temp is TextChannel) context.i18n("commands.unlock.text_success", temp.name) else name?.let { context.i18n("commands.unlock.success", channel.name, it) })
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