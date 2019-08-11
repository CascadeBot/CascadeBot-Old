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

    /**
     * Gets the amount of votes this got.
     *
     * @return the amount of votes this got.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Gets the unicode or {@link net.dv8tion.jda.api.entities.Emote} that was this vote.
     *
     * @return the unicode or {@link net.dv8tion.jda.api.entities.Emote} that was this vote.
     */
    public Object getVote() {
        return vote;
    }

}
