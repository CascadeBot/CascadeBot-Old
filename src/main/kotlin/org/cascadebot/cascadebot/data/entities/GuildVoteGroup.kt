/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.cascadebot.cascadebot.utils.votes.VoteFinishFunction
import org.cascadebot.cascadebot.utils.votes.VotePeriodicFunction
import org.hibernate.annotations.Type
import java.time.Instant
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

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
    val allowedUsers: MutableList<GuildVoteAllowedUser> = mutableListOf()

    @OneToMany
    @JoinColumn(name = "vote_group_id", referencedColumnName = "id")
    val votes: MutableList<GuildVoteVotes> = mutableListOf()

}