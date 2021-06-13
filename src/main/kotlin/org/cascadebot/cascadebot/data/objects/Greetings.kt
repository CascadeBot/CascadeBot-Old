package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.utils.lists.WeightedList
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects

class Greetings {

    

    var welcomeMessages: WeightedList<String> = WeightedList()
    var welcomeDMMessages: WeightedList<String> = WeightedList()
    var goodbyeMessages: WeightedList<String> = WeightedList()

    private var welcomeChannelId: Long? = null

    var welcomeChannel: TextChannel?
        get() = welcomeChannelId?.let(CascadeBot.INS.shardManager::getTextChannelById)
        set(channel) {
            welcomeChannelId = channel?.idLong
        }

    val welcomeEnabled: Boolean
        get() = welcomeChannelId != null && welcomeMessages.size > 0

    val welcomeDMEnabled: Boolean
        get() = welcomeDMMessages.size > 0

    val goodbyeEnabled: Boolean
        get() = goodbyeMessages.size > 0

    fun getRandomWelcomeMsg(event: GuildMemberJoinEvent): String? {
        return welcomeMessages.randomItem?.let {
            PlaceholderObjects.welcomes.formatMessage(Language.getGuildLocale(event.guild.idLong), it, event)
        }
    }

    fun getRandomWelcomeDMMsg(event: GuildMemberJoinEvent): String? {
        return welcomeDMMessages.randomItem?.let {
            PlaceholderObjects.welcomes.formatMessage(Language.getGuildLocale(event.guild.idLong), it, event)
        }
    }

    fun getRandomGoodbyeMsg(event: GuildMemberRemoveEvent): String? {
        return goodbyeMessages.randomItem?.let {
            PlaceholderObjects.goodbyes.formatMessage(Language.getGuildLocale(event.guild.idLong), it, event)
        }
    }


}