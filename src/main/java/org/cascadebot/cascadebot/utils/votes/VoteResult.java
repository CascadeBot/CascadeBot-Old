/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import org.jetbrains.annotations.NotNull;

public class VoteResult implements Comparable<VoteResult> {

    int amount;
    private Object vote;

    public VoteResult(int amount, Object vote) {
        this.amount = amount;
        this.vote = vote;
    }

    public int getAmount() {
        return amount;
    }

    public Object getVote() {
        return vote;
    }

    @Override
    public int compareTo(@NotNull VoteResult o) {
        return Integer.compare(this.amount, o.amount);
    }

}
