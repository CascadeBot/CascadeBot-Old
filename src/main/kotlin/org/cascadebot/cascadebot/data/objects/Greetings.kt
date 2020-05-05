package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.utils.ChangeList
import org.cascadebot.cascadebot.utils.WeightedList

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



}