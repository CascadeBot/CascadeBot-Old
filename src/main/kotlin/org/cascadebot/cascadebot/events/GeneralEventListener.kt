/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.StatusChangeEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.apache.commons.lang3.StringUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects

class GeneralEventListener : ListenerAdapter() {

    override fun onReady(event: ReadyEvent) {
        val shardManager = CascadeBot.INS.shardManager
        if (shardManager.shards.size == shardManager.shardsTotal) {
            CascadeBot.INS.run()
            Config.INS.eventWebhook.send(
                    MessageType.SUCCESS.emoji + " All shards ready!"
            )
        }
    }

    override fun onStatusChange(event: StatusChangeEvent) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (event.newStatus) {
            JDA.Status.CONNECTED,
            JDA.Status.DISCONNECTED,
            JDA.Status.RECONNECT_QUEUED,
            JDA.Status.ATTEMPTING_TO_RECONNECT,
            JDA.Status.SHUTTING_DOWN,
            JDA.Status.SHUTDOWN,
            JDA.Status.FAILED_TO_LOGIN -> Config.INS.eventWebhook.send(String.format(
                    UnicodeConstants.ROBOT + " Status Update: `%s` to `%s` on shard: `%d`",
                    FormatUtils.formatEnum(event.oldStatus),
                    FormatUtils.formatEnum(event.newStatus),
                    event.jda.shardInfo.shardId
            ))
        }
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
        if (!StringUtils.isBlank(Config.INS.guildWelcomeMessage)) {
            event.guild.owner!!
                    .user
                    .openPrivateChannel()
                    .queue({ it.sendMessage(Config.INS.guildWelcomeMessage).queue() }, { /* Do nothing on error! */ })
        }
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
                // .randomItem should only return null if there are no messages so if is null we want an error
                greetings.welcomeMessages.randomItem!!.let {
                    PlaceholderObjects.welcomes.formatMessage(it, event)
                }.let { message -> it.sendMessage(message).queue() }
            } ?: run { greetings.welcomeChannel = null }
        }

        val iterator = guildData.management.autoRoles.iterator()
        while (iterator.hasNext()) {
            val nextRoleId = iterator.next()
            val role = event.guild.getRoleById(nextRoleId)
            if (role != null) {
                event.guild.addRoleToMember(event.member, role).queue(null, null)
            } else {
                iterator.remove()
            }
        }
    }

    override fun onGuildMemberLeave(event: GuildMemberLeaveEvent) {
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        val greetings = guildData.management.greetings
        if (greetings.goodbyeEnabled) {
            greetings.goodbyeChannel?.let {
                // .randomItem should only return null if there are no messages so if is null we want an error
                greetings.goodbyeMessages.randomItem!!.let {
                    PlaceholderObjects.goodbyes.formatMessage(it, event)
                }.let { message -> it.sendMessage(message).queue() }
            } ?: run { greetings.goodbyeChannel = null }
        }
    }

    override fun onRoleDelete(event: RoleDeleteEvent) {
        for (group in GuildDataManager.getGuildData(event.guild.idLong).management.permissions.groups) {
            group.unlinkRole(event.role.idLong)
        }
    }
}