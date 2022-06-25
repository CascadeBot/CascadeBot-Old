/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import com.google.common.collect.Sets;
import de.bild.codec.annotations.Transient;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VoteGroup {

    private final Map<Long, Object> votes = new HashMap<>();

    private final VotePeriodicFunction periodicConsumer;
    private VoteFinishFunction finishConsumer;

    private Set<Long> allowedUsers;

    @Transient
    private final Timer timer = new Timer();

    @Transient
    private Timer voteTimer;

    private final long timerStartTime = Instant.now().toEpochMilli();

    private int timerRunTime;

    private int maxTimerRunTime;

    private int timerRunTimeSkipAddon;

    private boolean isDynamicTiming;

    public void setIsDynamicTiming(boolean isDynamicTiming) {
        this.isDynamicTiming = isDynamicTiming;
    }

    public void setFinishConsumer(VoteFinishFunction finishConsumer) {
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

    private final long ownerId;
    private final long channelId;
    private final long guildId;

    private long messageId = 0L;

    @Transient
    private Runnable messageSentAction;

    @Transient
    private ComponentContainer container;

    private String id;

    VoteGroup(String id, long ownerId, long channelId, long guildId, VoteFinishFunction finishConsumer, VotePeriodicFunction periodicRunnable, Timer voteTimer, ComponentContainer container) {
        this.ownerId = ownerId;
        this.channelId = channelId;
        this.guildId = guildId;
        this.periodicConsumer = periodicRunnable;
        if (periodicRunnable != null) {
            setUpVoteProcessConsumer();
        }
        this.finishConsumer = finishConsumer;
        this.voteTimer = voteTimer;
        this.container = container;
        this.id = id;
    }

    public ComponentContainer getComponents() {
        return container;
    }

    public void setMessageSentAction(Runnable messageSentAction) {
        this.messageSentAction = messageSentAction;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public long getGuildId() {
        return guildId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
        if (messageSentAction != null) {
            messageSentAction.run();
        }
    }

    private void setUpVoteProcessConsumer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CascadeBot.INS.getShardManager().getGuildById(guildId).getTextChannelById(channelId).retrieveMessageById(messageId).queue(message -> {
                    periodicConsumer.getConsumer().accept(getOrderedVoteResults(), message);
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
        } else if (isDynamicTiming) {
            dynamicTimingFunctionality(user);
        }
        votes.put(user.getIdLong(), vote);
    }

    private void dynamicTimingFunctionality(User user) {

        long timeElapsed = Instant.now().toEpochMilli() - timerStartTime;
        int elapsed = (int) FormatUtils.round((timeElapsed / 1000.0), 0);
        int newTimersTime;

        if (!user.isBot() && user.getIdLong() != ownerId) {
            if (timerRunTime < maxTimerRunTime) {
                timerRunTime = Math.max(timerRunTime + timerRunTimeSkipAddon, maxTimerRunTime);
                newTimersTime = timerRunTime - elapsed;

                voteTimer.cancel();
                voteTimer = new Timer();
                voteTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CascadeBot.INS.getShardManager().getGuildById(guildId).getTextChannelById(channelId).retrieveMessageById(messageId).queue(message -> {
                            message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                            voteFinished();
                            if (finishConsumer != null) {
                                finishConsumer.getConsumer().accept(CascadeBot.INS.getShardManager().getTextChannelById(channelId), getOrderedVoteResults());
                            }
                        }, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                    }
                }, TimeUnit.SECONDS.toMillis(newTimersTime));
            }
            Member member = CascadeBot.INS.getShardManager().getGuildById(guildId).getMember(user);

            double members = member.getVoiceState().getChannel().getMembers().size();
            int votesNeeded = (int) Math.ceil(members / 2.0);

            if ((votes.size()) >= votesNeeded - 1) { // - 1 is because the users vote hasn't been added yet
                CascadeBot.INS.getShardManager().getGuildById(guildId).getTextChannelById(channelId).retrieveMessageById(messageId).queue(message -> {
                    message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                    voteFinished();
                    if (finishConsumer != null) {
                        finishConsumer.getConsumer().accept(CascadeBot.INS.getShardManager().getTextChannelById(channelId), getOrderedVoteResults());
                    }
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
        return allowedUsers;
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
        /*GuildData guildData = GuildDataManager.getGuildData(CascadeBot.INS.getShardManager().getTextChannelById(channelId).getGuild().getIdLong());
        guildData.getVoteGroups().remove(id);*/

    }

    void voteFinished() {
        timer.cancel();
    }

}
