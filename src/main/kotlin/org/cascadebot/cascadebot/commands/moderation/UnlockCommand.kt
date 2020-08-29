package org.cascadebot.cascadebot.commands.moderation

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils
import java.util.*

class UnlockCommand : MainCommand() {
    override fun onCommand(sender: Member, context: CommandContext) {
        var channel: TextChannel = context.channel
        if (context.args.size == 2) {
            channel = DiscordUtils.getTextChannel(context.guild, context.getArg(1))
                    ?: return context.reply("Invalid channel")

        }

        val temp: ISnowflake? = if (context.args.isNotEmpty()) {
            DiscordUtils.getRole(context.getArg(0), context.guild)
                    ?: DiscordUtils.getMember(context.guild, context.getArg(0))
                    ?: DiscordUtils.getTextChannel(context.guild, context.getArg(0))
        } else {
            context.channel
        }
        var name: String? = null
        when (temp) {
            is Role -> {
                //channel.manager.putPermissionOverride(temp, null, EnumSet.of(Permission.MESSAGE_WRITE)).queue()
                name = temp.name
            }
            is Member -> {
                //channel.manager.putPermissionOverride(temp, null, EnumSet.of(Permission.MESSAGE_WRITE)).queue()
                name = temp.effectiveName
            }
            ;
            //is TextChannel -> temp.manager.putPermissionOverride(context.guild.publicRole, null, EnumSet.of(Permission.MESSAGE_WRITE)).queue()
            // ^ SET TO THE OLD PERMISSION BEFORE SOMEONE LOCKED THE CHANNEL FOR THE USER/ROLE
        }
        context.typedMessaging.replySuccess(if (temp is TextChannel) "%s unlocked".format(temp.name) else "Channel unlocked for %s".format(name))
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