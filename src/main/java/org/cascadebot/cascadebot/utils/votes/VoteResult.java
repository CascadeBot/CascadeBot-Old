/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

public class VoteResult {

    private int amount;
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

}
