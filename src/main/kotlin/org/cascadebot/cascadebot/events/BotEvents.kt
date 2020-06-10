/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.StatusChangeEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.utils.FormatUtils

class BotEvents : ListenerAdapter() {

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

}