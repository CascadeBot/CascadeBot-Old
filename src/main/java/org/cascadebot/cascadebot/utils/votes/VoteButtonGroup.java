/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.core.entities.User;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.HashMap;
import java.util.Map;

public class VoteButtonGroup extends ButtonGroup {

    Map<Long, Object> votes = new HashMap<>();

    public VoteButtonGroup(long ownerId, long channelId, long guildId) {
        super(ownerId, channelId, guildId);
    }

    public void addVote(User user, Object vote) {
        votes.put(user.getIdLong(), vote);
    }

    public Map<Long, Object> getVotes() {
        return votes;
    }
}
