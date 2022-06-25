/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.ErrorResponse
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.votes.VoteFinishFunction
import org.cascadebot.cascadebot.utils.votes.VotePeriodicFunction
import org.hibernate.annotations.Type
import java.time.Duration
import java.time.Instant
import java.util.Timer
import java.util.TimerTask
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table
import kotlin.math.ceil
import kotlin.math.min

@Entity
@Table(name = "guild_vote_group")
class GuildVoteGroup(type: String, guildId: Long, channelId: Long, messageId: Long, ownerId: Long, finishFunction: VoteFinishFunction) {

    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID()

    @Column(name = "type", nullable = false)
    var type: String = type

    @Column(name = "guild_id", nullable = false)
    var guildId: Long = guildId

    @Column(name = "channel_id", nullable = false)
    var channelId: Long = channelId

    @Column(name = "message_id", nullable = false)
    var messageId: Long = messageId

    @Column(name = "owner_id", nullable = false)
    var ownerId: Long = ownerId

    @Column(name = "start_time", nullable = true)
    var startTime: Instant? = null

    @Column(name = "run_duration", nullable = false)
    var runDuration: Int = 0

    @Column(name = "max_duration", nullable = false)
    var maxDuration: Int = 0

    @Column(name = "duration_increment", nullable = false)
    var durationIncrement: Int = TODO("Default value?")

    @Column(name = "dynamic_timing", nullable = false)
    var dynamicTiming: Boolean = false

    @Column(name =  "periodic_function", nullable = true)
    @Enumerated(EnumType.STRING)
    @Type(type = "psql-enum")
    var periodicFunction: VotePeriodicFunction? = null

    @Column(name =  "finish_function", nullable = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "psql-enum")
    var finishFunction: VoteFinishFunction = finishFunction

    @OneToMany
    @JoinColumn(name = "vote_group_id", referencedColumnName = "id")
    var allowedUsers: MutableList<GuildVoteAllowedUser> = mutableListOf()

    @OneToMany
    @JoinColumn(name = "vote_group_id", referencedColumnName = "id")
    val votes: MutableList<GuildVoteVotes> = mutableListOf()

    // Orders all votes from largest to smallest order
    val orderedVotes: List<Pair<Pair<String, VoteType>, Int>>
        get() = votes.groupingBy { Pair(it.vote, it.type) }.eachCount().toList().sortedByDescending { it.second }

    fun addVote(user: User, vote: Any) {
        val voteIdentifier = getVoteObjectIdentifier(vote)
        if (votes.removeIf { it.userId == user.idLong && it.vote == voteIdentifier.first && it.type == voteIdentifier.second }) {
            return
        }

        if (dynamicTiming) {
            dynamicTimingFunctionality(user)
        }

        votes.add(GuildVoteVotes(this.id, user.idLong, voteIdentifier.first, voteIdentifier.second))
    }

    private fun getVoteObjectIdentifier(vote: Any) : Pair<String, VoteType> {
        return when(vote) {
            is String -> Pair(vote, VoteType.UNICODE)
            is Emote -> Pair(vote.id, VoteType.EMOTE)
            else -> error("Could not get identifier for vote object")
        }
    }

    private fun dynamicTimingFunctionality(user: User) {
        val timeElapsed: Long = Duration.between(Instant.now(), startTime).toMillis()

        if (!user.isBot && user.idLong != ownerId) {
            if (runDuration < maxDuration) {
                // The new duration is the smallest of the duration + increment or the max duration
                runDuration = min(runDuration + durationIncrement, maxDuration)

                val newTimersTime = runDuration - timeElapsed

                voteTimer.cancel()
                voteTimer = Timer()
                voteTimer.schedule(object : TimerTask() {
                    override fun run() {
                        CascadeBot.INS.shardManager.getGuildById(guildId)!!.getTextChannelById(channelId)!!
                            .retrieveMessageById(messageId).queue(
                                { message: Message ->
                                    message.delete()
                                        .queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
                                    voteFinished()
                                    if (finishConsumer != null) {
                                        finishConsumer.getConsumer().accept(
                                            CascadeBot.INS.shardManager.getTextChannelById(channelId),
                                            getOrderedVoteResults()
                                        )
                                    }
                                }, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE)
                            )
                    }
                }, TimeUnit.SECONDS.toMillis(newTimersTime))
            }
            val member = CascadeBot.INS.shardManager.getGuildById(guildId)!!.getMember(user)
            val members = member!!.voiceState!!.channel!!.members.size.toDouble()
            val votesNeeded = ceil(members / 2.0).toInt()
            if (votes.size >= votesNeeded - 1) { // - 1 is because the users vote hasn't been added yet
                CascadeBot.INS.shardManager.getGuildById(guildId)!!.getTextChannelById(channelId)!!
                    .retrieveMessageById(messageId).queue(
                        { message: Message ->
                            message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
                            voteFinished()
                            if (finishConsumer != null) {
                                finishConsumer.getConsumer()
                                    .accept(
                                        CascadeBot.INS.shardManager.getTextChannelById(channelId),
                                        getOrderedVoteResults()
                                    )
                            }
                            stopVote()
                        }, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE)
                    )
            }
        }
    }

    fun isUserAllowed(userId: Long): Boolean {
        return allowedUsers.any { it.userId == userId }
    }

    fun allowUser(userId: Long): Boolean {
        if (isUserAllowed(userId)) return false
        return allowedUsers.add(GuildVoteAllowedUser(this.id, userId))
    }

    fun denyUser(userId: Long): Boolean {
        if (!isUserAllowed(userId)) return false

        return allowedUsers.removeIf { it.userId == userId }
    }

}