/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events.modlog

import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.data.objects.ModlogEventData
import org.cascadebot.cascadebot.moderation.ModlogEmbedField
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart
import org.cascadebot.cascadebot.moderation.ModlogEvent

class UserModlogEventListener : ListenerAdapter() {

    override fun onUserUpdateName(event: UserUpdateNameEvent) {
        val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
        embedFieldList.add(
            ModlogEmbedField(
                false,
                "modlog.general.name",
                "modlog.general.small_change",
                event.oldName,
                event.newName
            )
        )
        val modlogEventData = ModlogEventData(ModlogEvent.USER_NAME_UPDATED, event.user, event.user, embedFieldList)
        for (guild in CascadeBot.INS.client.getMutualGuilds(event.user)) {
            val guildData = GuildDataManager.getGuildData(guild.idLong)
            guildData.moderation.sendModlogEvent(guild.idLong, modlogEventData)
        }
    }

    override fun onUserUpdateDiscriminator(event: UserUpdateDiscriminatorEvent) {
        val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
        embedFieldList.add(
            ModlogEmbedField(
                false,
                "modlog.member.discrim",
                "modlog.general.small_change",
                event.oldDiscriminator,
                event.newDiscriminator
            )
        )
        val modlogEventData =
            ModlogEventData(ModlogEvent.USER_DISCRIMINATOR_UPDATED, event.user, event.user, embedFieldList)
        for (guild in CascadeBot.INS.client.getMutualGuilds(event.user)) {
            val guildData = GuildDataManager.getGuildData(guild.idLong)
            guildData.moderation.sendModlogEvent(guild.idLong, modlogEventData)
        }
    }

}