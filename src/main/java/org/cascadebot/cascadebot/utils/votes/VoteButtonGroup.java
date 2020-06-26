/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.buttons.PersistentButtonGroup;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VoteButtonGroup extends PersistentButtonGroup {

    private Map<Long, Object> votes = new HashMap<>();

    private BiConsumer<List<VoteResult>, Message> periodicConsumer;

    private Consumer<List<VoteResult>> finishConsumer;

    private Set<Long> allowedUsers;

    private Timer timer = new Timer();

    private Timer voteTimer;

    private long timerStartTime = Instant.now().toEpochMilli();

    private int timerRunTime;

    private int maxTimerRunTime;

    private int timerRunTimeSkipAddon;

    private boolean isMusicSkip;

    public void setIsMusicSkip(Boolean isMusicSkip) {
        this.isMusicSkip = isMusicSkip;
    }

    public void setFinishConsumer(Consumer<List<VoteResult>> finishConsumer) {
        this.finishConsumer = finishConsumer;
    }

    public void setMaxTimeRunTime(int maxTimerRunTime) {
        this.maxTimerRunTime = maxTimerRunTime;
    }

    public void setTimerRunTimeSkipAddon(int timerRunTimeSkipAddon) {
        this.timerRunTimeSkipAddon = timerRunTimeSkipAddon;
    }

    public void setTimerRunTime(int timerRunTime) {
        this.timerRunTime = timerRunTime;
    }

    VoteButtonGroup(long ownerId, long channelId, long guildId, BiConsumer<List<VoteResult>, Message> periodicRunnable, Timer voteTimer) {
        super(ownerId, channelId, guildId);
        this.periodicConsumer = periodicRunnable;
        if (periodicRunnable != null) {
            setUpVoteProcessConsumer();
        }
        this.voteTimer = voteTimer;
    }

    private void setUpVoteProcessConsumer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CascadeBot.INS.getShardManager().getGuildById(getGuildId()).getTextChannelById(getChannelId()).retrieveMessageById(getMessageId()).queue(message -> {
                    periodicConsumer.accept(getOrderedVoteResults(), message);
                });
            }
        }, 5000, 5000);
    }

    /**
     * Adds a vote from a specified user.
     *
     * @param user The user this vote came from.
     * @param vote The unicode or {@link net.dv8tion.jda.api.entities.Emote} that is the vote you want to add.
     */
    public void addVote(User user, Object vote) {
        if (votes.containsKey(user.getIdLong())) {
            if (votes.get(user.getIdLong()).equals(vote)) {
                votes.remove(user.getIdLong());
                return;
            }
        } else if (isMusicSkip) {
            musicSkipExtraFunctionality(user);
        }
        votes.put(user.getIdLong(), vote);
    }

    private void musicSkipExtraFunctionality(User user) {

        long timeElapsed = Instant.now().toEpochMilli() - timerStartTime;
        int elapsed = (int) FormatUtils.round((timeElapsed /1000), 0);
        int newTimersTime;

        if (!user.isBot() && user.getIdLong() != getOwnerId()) {
            if (timerRunTime < maxTimerRunTime) {
                if ((timerRunTime + timerRunTimeSkipAddon) > maxTimerRunTime) {
                    timerRunTime = maxTimerRunTime;
                } else {
                    timerRunTime += timerRunTimeSkipAddon;
                }
                newTimersTime = timerRunTime - elapsed;

                voteTimer.cancel();
                voteTimer = new Timer();
                voteTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CascadeBot.INS.getShardManager().getGuildById(getGuildId()).getTextChannelById(getChannelId()).retrieveMessageById(getMessageId()).queue(message -> {
                            message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                            voteFinished();
                            finishConsumer.accept(getOrderedVoteResults());
                        }, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                    }
                }, TimeUnit.SECONDS.toMillis(newTimersTime));
            }
            Member member = CascadeBot.INS.getShardManager().getGuildById(getGuildId()).getMember(user);

            int votesNeeded = (int) Math.ceil(member.getVoiceState().getChannel().getMembers().size() / 2);

            if ((votes.size()) >= votesNeeded - 1) { // - 1 is because the users vote hasn't been added yet
                CascadeBot.INS.getShardManager().getGuildById(getGuildId()).getTextChannelById(getChannelId()).retrieveMessageById(getMessageId()).queue(message -> {
                    message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                    voteFinished();
                    finishConsumer.accept(getOrderedVoteResults());
                    stopVote();
                }, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
            }
        }
    }
    public Map<Long, Object> getVotes() {
        return votes;
    }

    public List<VoteResult> getOrderedVoteResults() {
        Map<Object, Integer> countMap = new HashMap<>();
        for (Object key : getVotes().values()) {
            if (countMap.containsKey(key)) {
                countMap.put(key, countMap.get(key) + 1);
            } else {
                countMap.put(key, 1);
            }
        }

        // Sorts all counts from largest to smallest value
        return countMap.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(entry -> new VoteResult(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    /**
     * Gets what users are allowed to vote.
     *
     * @return A list of users that are allowed to vote.
     */
    public Set<Long> getAllowedUsers() {
        if (allowedUsers == null) {
            allowedUsers = Sets.newConcurrentHashSet();
        }
        return Set.copyOf(allowedUsers);
    }

    public boolean isUserAllowed(long userId) {
        return allowedUsers == null || allowedUsers.contains(userId);
    }

    public boolean allowUser(long userId) {
        if (allowedUsers == null) {
            allowedUsers = Sets.newConcurrentHashSet();
        }
        return allowedUsers.add(userId);
    }

    public boolean denyUser(long userId) {
        if (allowedUsers == null) {
            return false;
        }
        return allowedUsers.remove(userId);
    }

    public void stopVote() {
        voteTimer.cancel();
        timer.cancel();
    }

    void voteFinished() {
        timer.cancel();
    }

}
