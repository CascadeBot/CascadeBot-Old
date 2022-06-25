/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "guild_vote_votes")
class GuildVoteVotes(voteGroupId: UUID, userId: Long, vote: String) {

    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID()

    @Column(name = "vote_group_id", nullable = false)
    val voteGroupId: UUID = voteGroupId

    @Column(name = "user_id", nullable = false)
    val userId: Long = userId

    @Column(name = "vote", nullable = false)
    val vote: String = vote

}