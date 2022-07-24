/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events

import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.apache.commons.lang3.StringUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.entities.GuildAutoRoleEntity
import org.cascadebot.cascadebot.data.entities.GuildGreetingChannelEntity
import org.cascadebot.cascadebot.data.entities.GuildGreetingEntity
import org.cascadebot.cascadebot.data.objects.GreetingType
import org.cascadebot.cascadebot.utils.listOf
import org.cascadebot.cascadebot.utils.lists.WeightedList

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
        //GuildDataManager.getGuildData(event.guild.idLong).pageCache.remove(event.messageIdLong)
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        if (!StringUtils.isBlank(Config.INS.guildGoodbyeMessage)) {
            event.guild.owner!!
                .user
                .openPrivateChannel()
                .queue({ it.sendMessage(Config.INS.guildGoodbyeMessage).queue() }, { /* Do nothing on error! */ })
        }
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {

        val greetings = CascadeBot.INS.postgresManager.transaction {
            return@transaction listOf(GuildGreetingEntity::class.java, "guild_id", event.guild.idLong)
        }

        val greetingMap: MutableMap<GreetingType, WeightedList<GuildGreetingEntity>> = mutableMapOf();

        if (greetings != null) {
            for (greeting in greetings) {
                if (greetingMap.contains(greeting.type)) {
                    greetingMap[greeting.type]!!.add(greeting, greeting.weight)
                } else {
                    val list: WeightedList<GuildGreetingEntity> = WeightedList()
                    list.add(greeting, greeting.weight)
                    greetingMap[greeting.type] = list;
                }
            }
        }

        for (entry in greetingMap) {
            if (entry.key == GreetingType.GOODBYE) {
                continue;
            }
            val toSend = entry.value.randomItem!!;

            if (entry.key == GreetingType.WELCOME) {
                var channelEntity = CascadeBot.INS.postgresManager.transaction {
                    get(GuildGreetingChannelEntity::class.java, event.guild.idLong)
                } ?: continue

                val channel = channelEntity.channelId?.let { CascadeBot.INS.client.getTextChannelById(it) } ?: continue
                channel.sendMessage(toSend.content).queue()
                continue
            }

            event.member.user.openPrivateChannel().queue {
                it.sendMessage(toSend.content).queue()
            }
        }

        val roles = CascadeBot.INS.postgresManager.transaction {
            return@transaction listOf(GuildAutoRoleEntity::class.java, "guild_id", event.guild.idLong)
        } ?: return
        for (nextRoleId in roles) {
            val role = event.guild.getRoleById(nextRoleId.roleId)
            if (role != null) {
                event.guild.addRoleToMember(event.member, role).queue(null, null)
            } else {
                CascadeBot.INS.postgresManager.transaction {
                    delete(nextRoleId)
                }
            }
        }
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val greetings = CascadeBot.INS.postgresManager.transaction {
            return@transaction listOf(GuildGreetingEntity::class.java, "guild_id", event.guild.idLong)
        }

        val greetingMap: MutableMap<GreetingType, WeightedList<GuildGreetingEntity>> = mutableMapOf();

        if (greetings != null) {
            for (greeting in greetings) {
                if (greetingMap.contains(greeting.type)) {
                    greetingMap[greeting.type]!!.add(greeting, greeting.weight)
                } else {
                    val list: WeightedList<GuildGreetingEntity> = WeightedList()
                    list.add(greeting, greeting.weight)
                    greetingMap[greeting.type] = list;
                }
            }
        }

        for (entry in greetingMap) {
            if (entry.key != GreetingType.GOODBYE) {
                continue;
            }
            val toSend = entry.value.randomItem!!;

            event.user.openPrivateChannel().queue {
                it.sendMessage(toSend.content).queue()
            }
        }
    }

}