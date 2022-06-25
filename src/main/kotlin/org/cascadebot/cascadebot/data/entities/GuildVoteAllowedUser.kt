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
@Table(name = "guild_vote_allowed_users")
class GuildVoteAllowedUser(voteGroupId: UUID, userId: Long) {

    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID()

    @Column(name =  "vote_group_id", nullable = false)
    var voteGroupId: UUID = voteGroupId

    @Column(name =  "user_id", nullable = false)
    val userId: Long = userId

}