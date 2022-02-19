/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.core

import com.sun.management.OperatingSystemMXBean
import net.dv8tion.jda.api.entities.Member
import org.apache.commons.io.FileUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.CoreCommand
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.utils.FormatUtils
import java.lang.management.ManagementFactory
import kotlin.math.roundToInt

class StatsCommand : CoreCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {

        val builder = MessagingObjects.getClearThreadLocalEmbedBuilder(context.user, context.locale)
        builder.setTitle(context.selfUser.name)
        builder.setThumbnail(context.selfUser.effectiveAvatarUrl)

        val osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)

        val shardManager = CascadeBot.INS.shardManager
        val fields = mapOf(
                "total_guilds"          to shardManager.guilds.size.toString(),
                "active_guilds"         to shardManager.guildCache.size().toString(),
                "ram_usage"             to FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()),
                "cpu_load"              to "${(osBean.processCpuLoad * 100).roundToInt()}%",
                "total_shards"          to shardManager.shardsTotal.toString(),
                "online_shards"         to shardManager.shardsRunning.toString(),
                "gateway_ping"          to "${context.channel.jda.gatewayPing}ms",
                "rest_ping"             to "${context.channel.jda.restPing.complete()}ms",
                "shard_status"          to FormatUtils.formatEnum(shardManager.getStatus(context.channel.jda.shardInfo.shardId), context.locale),
                "shard_id"              to (context.channel.jda.shardInfo.shardId + 1).toString()
        )

        fields.forEach {
            builder.addField(context.i18n("commands.stats.embed.${it.key}"), it.value, true)
        }

        context.typedMessaging.replyInfo(builder)

    }

    override fun command(): String = "stats"
}