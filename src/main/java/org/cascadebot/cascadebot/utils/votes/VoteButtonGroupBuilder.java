/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.utils.buttons.Button;

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

    private List<Button> extraButtonList = new ArrayList<>();
    private List<Object> voteButtons = new ArrayList<>();
    private int amount = 1; //Setting this so things don't break

    private long voteTime = TimeUnit.SECONDS.toMillis(30);

    private Consumer<List<VoteResult>> finishConsumer;

    private BiConsumer<List<VoteResult>, Message> periodicConsumer;

    public VoteButtonGroupBuilder(VoteMessageType type) {
        this.type = type;
    }

    public VoteButtonGroupBuilder setOptionsAmount(int amount) {
        if (type == VoteMessageType.YES_NO || type == VoteMessageType.CUSTOM) {
            throw new UnsupportedOperationException("Cannot set options amount for yes no votes, or custom votes");
        }

        if (type == VoteMessageType.LETTERS && amount > 26) {
            throw new UnsupportedOperationException("Cannot have more then 26 options when using letters");
        }

        if (type == VoteMessageType.NUMBERS && amount > 9) {
            throw new UnsupportedOperationException("Cannot have more then 9 options when using numbers");
        }

        this.amount = amount;

        return this;
    }

    public VoteButtonGroupBuilder addExtraButton(Button button) {
        extraButtonList.add(button);

        return this;
    }

    public VoteButtonGroupBuilder addVoteButtonUnicode(String unicode) {
        if (type != VoteMessageType.CUSTOM) {
            throw new UnsupportedOperationException("Cannot add vote buttons to any type besides custom");
        }

        voteButtons.add(unicode);

        return this;
    }

    public VoteButtonGroupBuilder addVoteButtonEmote(Emote emote) {
        if (type != VoteMessageType.CUSTOM) {
            throw new UnsupportedOperationException("Cannot add vote buttons to any type besides custom");
        }

        voteButtons.add(emote);

        return this;
    }

    public VoteButtonGroupBuilder setVoteTime(long time) {
        this.voteTime = time;
        return this;
    }

    public VoteButtonGroupBuilder setVoteFinishConsumer(Consumer<List<VoteResult>> finishConsumer) {
        this.finishConsumer = finishConsumer;
        return this;
    }

    public VoteButtonGroupBuilder setPeriodicConsumer(BiConsumer<List<VoteResult>, Message> periodicConsumer) {
        this.periodicConsumer = periodicConsumer;
        return this;
    }

    public VoteButtonGroup build(long owner, long channelId, long guild) {
        VoteButtonGroup buttonGroup = new VoteButtonGroup(owner, channelId, guild, periodicConsumer, timer);
        switch (type) {
            case YES_NO:
                buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
                    if (!buttonGroup.isUserAllowed(runner.getUser().getIdLong())) {
                        return;
                    }
                    buttonGroup.addVote(runner.getUser(), UnicodeConstants.TICK);
                }));
                buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.RED_CROSS, (runner, channel, message) -> {
                    if (!buttonGroup.isUserAllowed(runner.getUser().getIdLong())) {
                        return;
                    }
                    buttonGroup.addVote(runner.getUser(), UnicodeConstants.RED_CROSS);
                }));
                break;
            case NUMBERS:
                for (int i = 1; i <= amount; i++) {
                    char unicode = (char) (0x0030 + i); //This is setting up the first unicode character to be 003n where n is equal to i.
                    final int num = i;
                    buttonGroup.addButton(new Button.UnicodeButton(unicode + "\u20E3", (runner, channel, message) -> {
                        if (!buttonGroup.isUserAllowed(runner.getUser().getIdLong())) {
                            return;
                        }
                        buttonGroup.addVote(runner.getUser(), num);
                    }));
                }
                break;
            case LETTERS:
                for (int i = 0; i < amount; i++) {
                    char unicode = (char) (0xdde0 + (i + 6));
                    final int num = i;
                    buttonGroup.addButton(new Button.UnicodeButton("\uD83C" + unicode, (runner, channel, message) -> {
                        if (!buttonGroup.isUserAllowed(runner.getUser().getIdLong())) {
                            return;
                        }
                        buttonGroup.addVote(runner.getUser(), num);
                    }));
                }
                break;
            case CUSTOM:
                for (Object object : voteButtons) {
                    if (object instanceof Emote) {
                        Emote emote = (Emote) object;
                        buttonGroup.addButton(new Button.EmoteButton(emote.getIdLong(), (runner, channel, message) -> {
                            if (!buttonGroup.isUserAllowed(runner.getUser().getIdLong())) {
                                return;
                            }
                            buttonGroup.addVote(runner.getUser(), emote.getIdLong());
                        }));
                    } else {
                        String unicode = (String) object;
                        buttonGroup.addButton(new Button.UnicodeButton(unicode, (runner, channel, message) -> {
                            if (!buttonGroup.isUserAllowed(runner.getUser().getIdLong())) {
                                return;
                            }
                            buttonGroup.addVote(runner.getUser(), unicode);
                        }));
                    }
                }
                break;
        }

        for (Button button : extraButtonList) {
            buttonGroup.addButton(button);
        }

        buttonGroup.setMessageSentAction(() -> {
            if (!sent) {
                sent = true;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CascadeBot.INS.getShardManager().getGuildById(buttonGroup.getGuildId()).getTextChannelById(buttonGroup.getChannelId()).getMessageById(buttonGroup.getMessageId()).queue(message -> {
                            message.delete().queue(null, ignoredFailure -> {});
                        }, ignoredFailure -> {});
                        buttonGroup.voteFinished();
                        finishConsumer.accept(buttonGroup.getOrderedVoteResults());
                    }
                }, voteTime);
            }
        });

        return buttonGroup;
    }

}