/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.objects.VoteMessageType;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.buttons.PersistentButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VoteButtonGroupBuilder {

    private VoteMessageType type;

    private Timer timer = new Timer();

    private boolean sent = false;

    private List<PersistentButton> extraButtonList = new ArrayList<>();
    private List<Object> voteButtons = new ArrayList<>();
    private int amount = 1; //Setting this so things don't break

    private long voteTime = TimeUnit.SECONDS.toMillis(10);

    private Consumer<List<VoteResult>> finishConsumer;

    private BiConsumer<List<VoteResult>, Message> periodicConsumer;

    private boolean isMusicSkip = false;

    public void setIsMusicSkip(boolean isMusicSkip) {
        this.isMusicSkip = isMusicSkip;
    }

    /**
     * Creates a new build for {@link VoteButtonGroup}
     *
     * @param type The {@link VoteMessageType} to use for voting
     */
    public VoteButtonGroupBuilder(VoteMessageType type) {
        this.type = type;
    }

    /**
     * Sets the amount of vote options to give. Used when using the {@link VoteMessageType#NUMBERS} or {@link VoteMessageType#LETTERS} types.
     * When using {@link VoteMessageType#NUMBERS} your limited between 1 and 9 options.
     * When using {@link VoteMessageType#LETTERS} your limited between 1 and 26 options.
     *
     * @param amount The amount of options to have.
     * @return this.
     */
    public VoteButtonGroupBuilder setOptionsAmount(int amount) {
        if (type == VoteMessageType.YES_NO) {
            throw new UnsupportedOperationException("Cannot set options amount for yes no votes");
        }

        if (type == VoteMessageType.LETTERS && amount > 15) { //This is because discord has a limit of 20 emotes per message, and I decided to have 5 extra for non vote buttons
            throw new UnsupportedOperationException("Cannot have more then 15 options when using letters");
        }

        if (type == VoteMessageType.NUMBERS && amount > 9) {
            throw new UnsupportedOperationException("Cannot have more then 9 options when using numbers");
        }

        this.amount = amount;

        return this;
    }

    /**
     * Add an extra non-vote related button.
     *
     * @param button the non-vote related button to add.
     * @return this.
     */
    public VoteButtonGroupBuilder addExtraButton(PersistentButton button) {
        extraButtonList.add(button);

        return this;
    }

    /**
     * Set how long the vote will run for.
     *
     * @param time The length in ms that the vote will run fo.
     * @return this.
     */
    public VoteButtonGroupBuilder setVoteTime(long time) {
        this.voteTime = time;
        return this;
    }

    /**
     * Sets the consumer to be run when the vote finishes.
     * This returns an ordered list of {@link VoteResult}s with the reaction with the most votes being ad the top.
     *
     * @param finishConsumer The {@link Consumer}.
     * @return this.
     */
    public VoteButtonGroupBuilder setVoteFinishConsumer(Consumer<List<VoteResult>> finishConsumer) {
        this.finishConsumer = finishConsumer;
        return this;
    }

    /**
     * Sets the consumer to be run every 5 seconds the vote is running.
     * This returns an ordered list of {@link VoteResult}s with the reaction with the most votes being ad the top.
     *
     * @param periodicConsumer The {@link Consumer}.
     * @return this.
     */
    public VoteButtonGroupBuilder setPeriodicConsumer(BiConsumer<List<VoteResult>, Message> periodicConsumer) {
        this.periodicConsumer = periodicConsumer;
        return this;
    }

    /**
     * Builds the {@link VoteButtonGroup}
     *
     * @param owner     The owner/initiator of the vote.
     * @param channelId The channel in witch the vote takes place.
     * @param guild     The guild in witch the vote is taking place.
     * @return a {@link VoteButtonGroup}.
     */
    public VoteButtonGroup build(long owner, long channelId, long guild) {
        VoteButtonGroup buttonGroup = new VoteButtonGroup(owner, channelId, guild, periodicConsumer, timer);
        buttonGroup.setIsMusicSkip(isMusicSkip);
        if (isMusicSkip) {
            buttonGroup.setFinishConsumer(finishConsumer);
            buttonGroup.setTimerRunTime(10);
            buttonGroup.setMaxTimeRunTime(30);
            buttonGroup.setTimerRunTimeSkipAddon(5);
        }
        switch (type) {
            case YES_NO:
                buttonGroup.addPersistentButton(PersistentButton.VOTE_BUTTON_YES);
                buttonGroup.addPersistentButton(PersistentButton.VOTE_BUTTON_NO);
                break;
            case NUMBERS:
                for (int i = 0; i < amount; i++) {
                    buttonGroup.addPersistentButton(PersistentButton.values()[PersistentButton.VOTE_BUTTON_ONE.ordinal() + i]);
                }
                break;
            case LETTERS:
                for (int i = 0; i < amount; i++) {
                    buttonGroup.addPersistentButton(PersistentButton.values()[PersistentButton.VOTE_BUTTON_A.ordinal() + i]);
                }
                break;
        }

        for (PersistentButton button : extraButtonList) {
            buttonGroup.addPersistentButton(button);
        }

        buttonGroup.setMessageSentAction(() -> {
            if (!sent) {
                sent = true;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CascadeBot.INS.getShardManager().getGuildById(buttonGroup.getGuildId()).getTextChannelById(buttonGroup.getChannelId()).retrieveMessageById(buttonGroup.getMessageId()).queue(message -> {
                            message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                        }, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                        buttonGroup.voteFinished();
                        finishConsumer.accept(buttonGroup.getOrderedVoteResults());
                    }
                }, voteTime);
            }
        });

        return buttonGroup;
    }
}
