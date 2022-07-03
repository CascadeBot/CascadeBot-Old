/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes

import jdk.internal.joptsimple.internal.Messages.message
import net.dv8tion.jda.api.entities.Message
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.messaging.MessagingObjects


enum class VotePeriodicFunction(val function: (Message, List<VoteResult>)->Unit) {
    SKIP({ message, results ->
        val resultsBuilder = StringBuilder()
        for ((vote, _, count) in results) {
            resultsBuilder.append(vote).append(" (").append(count).append(")\n")
        }
        /*val data: GuildData = GuildDataManager.getGuildData(message.guild.idLong)
        val group: VoteGroup = data.getVoteGroups().get("skip")
        CascadeBot.INS.shardManager.retrieveUserById(group.getOwnerId())
            .queue { user: net.dv8tion.jda.api.entities.User? ->
                val skipVoteEmbed: EmbedBuilder =
                    MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, user, data.getLocale())
                        .setTitle(data.getLocale().i18n("commands.skip.skip_vote_title"))
                if (resultsBuilder.length > 0) {
                    skipVoteEmbed.setDescription(resultsBuilder.toString())
                }
                message.editMessage(skipVoteEmbed.build()).override(true).setActionRows(message.getActionRows()).queue()
            }*/
    });

}
