/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.VoteMessageType;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.interactions.PersistentComponent;
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow;
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VoteGroupBuilder {

    private VoteMessageType type;
    private String id;

    private Timer timer = new Timer();

    private boolean sent = false;

    private List<PersistentComponent> extraButtonList = new ArrayList<>();
    private List<Object> voteButtons = new ArrayList<>();
    private int amount = 1; //Setting this so things don't break

    private long voteTime = TimeUnit.SECONDS.toMillis(10);

    private VoteFinishConsumer finishConsumer;

    private VotePeriodicConsumer periodicConsumer;

    /**
     * Creates a new build for {@link VoteGroup}
     *
     * @param type The {@link VoteMessageType} to use for voting
     */
    public VoteGroupBuilder(VoteMessageType type, String id) {
        this.type = type;
        this.id = id;
    }

    /**
     * Sets the amount of vote options to give. Used when using the {@link VoteMessageType#NUMBERS} or {@link VoteMessageType#LETTERS} types.
     * When using {@link VoteMessageType#NUMBERS} your limited between 1 and 9 options.
     * When using {@link VoteMessageType#LETTERS} your limited between 1 and 26 options.
     *
     * @param amount The amount of options to have.
     * @return this.
     */
    public VoteGroupBuilder setOptionsAmount(int amount) {
        if (amount < 2) {
            throw new UnsupportedOperationException("Cannot have less then 2 options");
        }

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
    public VoteGroupBuilder addExtraButton(PersistentComponent button) {
        extraButtonList.add(button);

        return this;
    }

    /**
     * Set how long the vote will run for.
     *
     * @param time The length in ms that the vote will run fo.
     * @return this.
     */
    public VoteGroupBuilder setVoteTime(long time) {
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
    public VoteGroupBuilder setVoteFinishConsumer(VoteFinishConsumer finishConsumer) {
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
    public VoteGroupBuilder setPeriodicConsumer(VotePeriodicConsumer periodicConsumer) {
        this.periodicConsumer = periodicConsumer;
        return this;
    }

    /**
     * Builds the {@link VoteGroup}
     *
     * @param owner     The owner/initiator of the vote.
     * @param channelId The channel in witch the vote takes place.
     * @param guild     The guild in witch the vote is taking place.
     * @return a {@link VoteGroup}.
     */
    public VoteGroup build(long owner, long channelId, long guild) {
        ComponentContainer container = new ComponentContainer();
        switch (type) {
            case YES_NO:
                CascadeActionRow row = new CascadeActionRow();
                row.addComponent(PersistentComponent.VOTE_BUTTON_YES.getComponent());
                row.addComponent(PersistentComponent.VOTE_BUTTON_NO.getComponent());
                container.addRow(row);
                break;
            case NUMBERS:
                CascadeActionRow rowN = null;
                for (int iN = 0; iN < amount; iN++) {
                    if (iN % 5 == 0) {
                        if (rowN != null) container.addRow(rowN);
                        rowN = new CascadeActionRow();
                    }
                    rowN.addComponent(PersistentComponent.values()[PersistentComponent.VOTE_BUTTON_ONE.ordinal() + iN].getComponent());
                }
                if (rowN != null) {
                    container.addRow(rowN);
                }
                break;
            case LETTERS:
                CascadeActionRow rowL = null;
                for (int i = 0; i < amount; i++) {
                    if (i % 5 == 0) {
                        if (rowL != null) container.addRow(rowL);
                        rowL = new CascadeActionRow();
                    }
                    rowL.addComponent(PersistentComponent.values()[PersistentComponent.VOTE_BUTTON_A.ordinal() + i].getComponent());
                }
                if (rowL != null) {
                    container.addRow(rowL);
                }
                break;
        }

        CascadeActionRow row = new CascadeActionRow();
        for (PersistentComponent button : extraButtonList) {
            row.addComponent(button.getComponent());
        }
        container.addRow(row);
        VoteGroup voteGroup = new VoteGroup(id, owner, channelId, guild, finishConsumer, periodicConsumer, timer, container);

        GuildData data =  GuildDataManager.getGuildData(guild);
        if (data.getVoteGroups().containsKey(id)) {
            throw new UnsupportedOperationException("Cannot have multiple votes with the same id in a guild!");
        } else {
            data.getVoteGroups().put(id, voteGroup);
        }

        voteGroup.setMessageSentAction(() -> {
            if (!sent) {
                sent = true;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CascadeBot.INS.getShardManager().getGuildById(voteGroup.getGuildId()).getTextChannelById(voteGroup.getChannelId()).retrieveMessageById(voteGroup.getMessageId()).queue(message -> {
                            message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                        }, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                        voteGroup.voteFinished();
                        finishConsumer.getConsumer().accept(CascadeBot.INS.getShardManager().getTextChannelById(channelId), voteGroup.getOrderedVoteResults());
                    }
                }, voteTime);
            }
        });

        return voteGroup;
    }
}
