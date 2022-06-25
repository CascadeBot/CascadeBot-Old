/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild_vote_votes")
class GuildVoteVotes(voteGroupId: UUID, userId: Long, vote: String, voteType: VoteType) {

    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID()

    @Column(name = "vote_group_id", nullable = false)
    val voteGroupId: UUID = voteGroupId

    @Column(name = "user_id", nullable = false)
    val userId: Long = userId

    @Column(name = "vote", nullable = false)
    val vote: String = vote

    @Column(name =  "type", nullable = false)
    @Type(type = "psql-enum")
    @Enumerated(EnumType.STRING)
    val type: VoteType = voteType

}

enum class VoteType { UNICODE, EMOTE }