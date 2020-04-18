package org.cascadebot.cascadebot.commandmeta

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import org.apache.commons.lang3.ArrayUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.objects.GuildData
import org.cascadebot.cascadebot.messaging.MessagingDirectMessage
import org.cascadebot.cascadebot.messaging.MessagingTimed
import org.cascadebot.cascadebot.messaging.MessagingTyped
import org.cascadebot.cascadebot.messaging.MessagingUI
import org.cascadebot.cascadebot.music.CascadePlayer

class CommandContext(
        val data: GuildData,
        val command: ICommandExecutable,
        val jda: JDA,
        val channel: MessageChannel,
        val message: Message,
        val guild: Guild,
        val member: Member,
        val args: Array<String>,
        var trigger: String,
        val mention: Boolean
) {
    val messagingTyped = MessagingTyped(this)
    val messagingTimed = MessagingTimed(this)
    val messagingUI = MessagingUI(this)
    val messagingDirectMessage = MessagingDirectMessage(this)

    val locale : Locale
        get() = data.locale

    val musicPlayer : CascadePlayer
        get() = CascadeBot.INS.musicHandler.getPlayer(guild.idLong)

    val user : User
        get() = member.user

    fun message(start: Int, end: Int = args.size) : String {
        return ArrayUtils.subarray(args, start, end).joinToString(" ")
    }

    fun arg(index: Int) : String = args[index]




}
