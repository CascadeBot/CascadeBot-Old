package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class SlowmodeResetSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        var channel: TextChannel = context.channel
        if (!context.args.isEmpty()) {
            try {
                channel = DiscordUtils.getTextChannel(context.guild, context.getArg(0))
            } catch (e: IllegalStateException) {
                context.typedMessaging.replyWarning(context.i18n("commands.slowmode.invalid_channel", context.getArg(1)))
                return
            }
        }

        if (!context.selfMember.hasPermission(context.channel, Permission.MANAGE_CHANNEL)) {
            context.uiMessaging.sendBotDiscordPermError(Permission.MANAGE_CHANNEL)
            return
        }
        channel.manager.setSlowmode(0).queue() // Slowmode off
        context.typedMessaging.replySuccess(context.i18n("commands.slowmode.reset_success", channel.name))
    }

    override fun parent(): String = "slowmode"

    override fun command(): String = "reset"

    override fun permission(): CascadePermission? = CascadePermission.of("slowmode.reset", false, Permission.MANAGE_CHANNEL)

}