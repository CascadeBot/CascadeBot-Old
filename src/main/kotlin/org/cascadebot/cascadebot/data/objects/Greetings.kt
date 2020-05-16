package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.utils.ChangeList
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.WeightedList
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects
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
        get() = welcomeChannelId != null && welcomeMessages.size > 0

    val goodbyeEnabled: Boolean
        get() = goodbyeChannelId != null && goodbyeMessages.size > 0

    fun getRandomWelcomeMsg(event: GuildMemberJoinEvent): String? {
        return welcomeMessages.randomItem?.let {
            PlaceholderObjects.welcomes.formatMessage(it, event)
        }
    }

    fun getRandomGoodbyeMsg(event: GuildMemberLeaveEvent): String? {
        return goodbyeMessages.randomItem?.let {
            PlaceholderObjects.goodbyes.formatMessage(it, event)
        }
    }


}