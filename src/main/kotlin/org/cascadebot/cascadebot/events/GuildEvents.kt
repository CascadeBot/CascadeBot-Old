/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events

import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.ErrorResponse
import org.apache.commons.lang3.StringUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.utils.DiscordUtils
import java.util.function.Consumer

class GuildEvents : ListenerAdapter() {

    override fun onGuildJoin(event: GuildJoinEvent) {
        if (!StringUtils.isBlank(Config.INS.guildWelcomeMessage)) {
            event.guild.owner!!
                    .user
                    .openPrivateChannel()
                    .queue({ it.sendMessage(Config.INS.guildWelcomeMessage).queue() }, { /* Do nothing on error! */ })
        }
    }

    override fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        GuildDataManager.getGuildData(event.guild.idLong).pageCache.remove(event.messageIdLong)
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        CascadeBot.INS.musicHandler.removePlayer(event.guild.idLong)
        if (!StringUtils.isBlank(Config.INS.guildGoodbyeMessage)) {
            event.guild.owner!!
                    .user
                    .openPrivateChannel()
                    .queue({ it.sendMessage(Config.INS.guildGoodbyeMessage).queue() }, { /* Do nothing on error! */ })
        }
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        val greetings = guildData.management.greetings
        if (greetings.welcomeEnabled) {
            greetings.welcomeChannel?.let {
                greetings.getRandomWelcomeMsg(event)?.let { message -> it.sendMessage(message).queue() }
            } ?: run { greetings.welcomeChannel = null }
        }

        /*if (greetings.welcomeDMEnabled) {
            greetings.getRandomWelcomeDMMsg(event)?.let { message ->
                event.user.takeUnless { it.isFake }
                        ?.let { channel -> channel.openPrivateChannel().queue(Consumer { it.sendMessage(message).queue() }, DiscordUtils.handleExpectedErrors(ErrorResponse.CANNOT_SEND_TO_USER)) }
            }
        }*/

        val iterator = guildData.management.autoRoles.iterator()
        for (nextRoleId in iterator) {
            val role = event.guild.getRoleById(nextRoleId)
            if (role != null) {
                event.guild.addRoleToMember(event.member, role).queue(null, null)
            } else {
                iterator.remove()
            }
        }
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        val greetings = guildData.management.greetings
        if (greetings.goodbyeEnabled) {
            /*greetings.getRandomGoodbyeMsg(event)?.let { message ->
                event.user.takeUnless { it.isFake }
                        ?.let { channel -> channel.openPrivateChannel().queue(Consumer { it.sendMessage(message).queue() }, DiscordUtils.handleExpectedErrors(ErrorResponse.CANNOT_SEND_TO_USER)) }
            }*/
        }
    }

    override fun onRoleDelete(event: RoleDeleteEvent) {
        for (group in GuildDataManager.getGuildData(event.guild.idLong).management.permissions.groups) {
            group.unlinkRole(event.role.idLong)
        }
    }
}