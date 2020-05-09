package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.utils.ChangeList
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.WeightedList
import java.lang.StringBuilder
import java.time.Duration
import java.time.OffsetDateTime

class Greetings {

    var welcomeMessages: WeightedList<String> = WeightedList()
    var goodbyeMessages: WeightedList<String> = WeightedList()

    private var welcomeChannelId: Long? = null
    private var goodbyeChannelId: Long? = null

    var welcomeChannel: TextChannel?
        get() = welcomeChannelId?.let(CascadeBot.INS.shardManager::getTextChannelById)
        set(channel) {
            welcomeChannelId = channel?.idLong
        }

    var goodbyeChannel: TextChannel?
        get() = goodbyeChannelId?.let(CascadeBot.INS.shardManager::getTextChannelById)
        set(channel) {
            goodbyeChannelId = channel?.idLong
        }

    val welcomeEnabled: Boolean
        get() = welcomeChannelId != null

    val goodbyeEnabled: Boolean
        get() = goodbyeChannelId != null

    fun getRandomWelcomeMsg(event: GuildMemberJoinEvent): String? {
        return welcomeMessages.randomItem?.let {
            val builder = StringBuilder(it)
            builder.replace(Regex("\\{guild_name}", RegexOption.IGNORE_CASE), event.guild.name)
            builder.replace(Regex("\\{user_name}", RegexOption.IGNORE_CASE), event.user.name)
            builder.replace(Regex("\\{user_mention}", RegexOption.IGNORE_CASE), event.user.asMention)
            builder.replace(Regex("\\{time}", RegexOption.IGNORE_CASE), FormatUtils.formatDateTime(OffsetDateTime.now()))
            builder.replace(Regex("\\{member_count}", RegexOption.IGNORE_CASE), event.guild.memberCache.size().toString())
            return@let builder.toString()
        }
    }
    fun getRandomGoodbyeMsg(event: GuildMemberLeaveEvent): String? {
        return goodbyeMessages.randomItem?.let {
            val builder = StringBuilder(it)
            builder.replace(Regex("\\{guild_name}", RegexOption.IGNORE_CASE), event.guild.name)
            builder.replace(Regex("\\{user_name}", RegexOption.IGNORE_CASE), event.user.name)
            builder.replace(Regex("\\{user_mention}", RegexOption.IGNORE_CASE), event.user.asMention)
            builder.replace(Regex("\\{time}", RegexOption.IGNORE_CASE), FormatUtils.formatDateTime(OffsetDateTime.now()))
            builder.replace(Regex("\\{member_count}", RegexOption.IGNORE_CASE), event.guild.memberCache.size().toString())
            builder.replace(Regex("\\{time_in_guild}", RegexOption.IGNORE_CASE), Duration.between(event.member.timeJoined, OffsetDateTime.now()).toString())
            builder.toString()
        }
    }


}