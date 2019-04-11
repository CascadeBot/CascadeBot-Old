/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.core.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VoteButtonGroup extends ButtonGroup {

    private Map<Long, Object> votes = new HashMap<>();

    private VoteButtonGroupBuilder.IVotePeriodicRunnable periodicRunnable;

    private Timer timer = new Timer();

    private Timer voteTimer;

    public VoteButtonGroup(long ownerId, long channelId, long guildId, VoteButtonGroupBuilder.IVotePeriodicRunnable votePeriodicRunnable, Timer voteTimer) {
        super(ownerId, channelId, guildId);
        this.periodicRunnable = votePeriodicRunnable;
        if (periodicRunnable != null) {
            setUpVoteProcessConsumer();
        }
        this.voteTimer = voteTimer;
    }

    private void setUpVoteProcessConsumer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CascadeBot.INS.getShardManager().getGuildById(getGuildId()).getTextChannelById(getChannelId()).getMessageById(getMessageId()).queue(message -> {
                    periodicRunnable.run(getOrderedVoteResults(), message);
                });
            }
        }, 5000, 5000);
    }

    public void addVote(User user, Object vote) {
        if (votes.containsKey(user.getIdLong())) {
            if (votes.get(user.getIdLong()).equals(vote)) {
                votes.remove(user.getIdLong());
                return;
            }
        }
        votes.put(user.getIdLong(), vote);
    }

    public Map<Long, Object> getVotes() {
        return votes;
    }

    public List<VoteResult> getOrderedVoteResults() {
        Map<Object, Integer> countMap = new HashMap<>();
        for (Map.Entry<Long, Object> entry : getVotes().entrySet()) {
            if (countMap.containsKey(entry.getValue())) {
                int value = countMap.get(entry.getValue()) + 1;
                countMap.put(entry.getValue(), value);
            } else {
                countMap.put(entry.getValue(), 1);
            }
        }
        List<VoteResult> voteResults = new ArrayList<>();
        for (Map.Entry<Object, Integer> entry : countMap.entrySet()) {
            voteResults.add(new VoteResult(entry.getValue(), entry.getKey()));
        }
        voteResults.sort(Collections.reverseOrder());
        return voteResults;
    }

    public void stopVote() {
        voteTimer.cancel();
        timer.cancel();
    }

    void voteFinished() {
        timer.cancel();
    }
    
}
