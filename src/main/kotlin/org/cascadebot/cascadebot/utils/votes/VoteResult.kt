/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes

import org.cascadebot.cascadebot.data.entities.VoteType

data class VoteResult(val vote: String, val voteType: VoteType, val count: Int) : Comparable<VoteResult> {
    override fun compareTo(other: VoteResult): Int {
        val countCompare = count.compareTo(other.count)
        if (countCompare != 0) {
            return  countCompare
        }

        val voteTypeCompare = voteType.compareTo(other.voteType)
        if (voteTypeCompare != 0) {
            return voteTypeCompare
        }

        return vote.compareTo(other.vote)
    }
}