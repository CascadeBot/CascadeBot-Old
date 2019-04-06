/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.core.entities.Emote;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class VoteButtonGroupBuilder {

    private VoteMessageType type;

    private List<Button> extraButtonList = new ArrayList<>();
    private List<Object> voteButtons = new ArrayList<>();
    private int amount;

    private long voteTime;

    private Map<Long, Object> voteMap = new HashMap<>();

    private Consumer<Object> finishConsumer;

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

    public VoteButtonGroupBuilder setVoteFinishConsumer(Consumer<Object> finishConsumer) {
        this.finishConsumer = finishConsumer;
        return this;
    }

    public ButtonGroup build(long owner, long channelId, long guild) {
        ButtonGroup buttonGroup = new ButtonGroup(owner, channelId, guild);
        switch (type) {
            case YES_NO:
                buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
                    voteMap.putIfAbsent(runner.getUser().getIdLong(), UnicodeConstants.TICK);
                }));
                buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.RED_CROSS, (runner, channel, message) -> {
                    voteMap.putIfAbsent(runner.getUser().getIdLong(), UnicodeConstants.RED_CROSS);
                }));
                break;
            case NUMBERS:
                for (int i = 1; i <= amount; i++) {
                    char unicode = (char) (0x0030 + i); //This is setting up the first unicode character to be 003n where n is equal to i.
                    final int num = i;
                    buttonGroup.addButton(new Button.UnicodeButton(unicode + "\u20E3", (runner, channel, message) -> {
                        voteMap.putIfAbsent(runner.getUser().getIdLong(), num);
                    }));
                }
                break;
            case LETTERS:
                for (int i = 0; i < amount; i++) {
                    char unicode = (char) (0xdde0 + (i + 6));
                    final int num = i;
                    buttonGroup.addButton(new Button.UnicodeButton("\uD83C" + unicode, (runner, channel, message) -> {
                        voteMap.putIfAbsent(runner.getUser().getIdLong(), num);
                    }));
                }
                break;
            case CUSTOM:
                for (Object object : voteButtons) {
                    if (object instanceof Emote) {
                        Emote emote = (Emote) object;
                        buttonGroup.addButton(new Button.EmoteButton(emote, (runner, channel, message) -> {
                            voteMap.putIfAbsent(runner.getUser().getIdLong(), emote.getIdLong());
                        }));
                    } else {
                        String unicode = (String) object;
                        buttonGroup.addButton(new Button.UnicodeButton(unicode, (runner, channel, message) -> {
                            voteMap.putIfAbsent(runner.getUser().getIdLong(), unicode);
                        }));
                    }
                }
                break;
        }

        for (Button button : extraButtonList) {
            buttonGroup.addButton(button);
        }

        new Thread(new VoteWaitRunnable(buttonGroup)).start();

        return buttonGroup;
    }

    public class VoteWaitRunnable implements Runnable {

        ButtonGroup voteGroup;

        public VoteWaitRunnable(ButtonGroup buttonGroup) {
            voteGroup = buttonGroup;
        }

        @Override
        public void run() {

            while (voteGroup.getMessageId() == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    CascadeBot.INS.getShardManager().getGuildById(voteGroup.getGuildId()).getTextChannelById(voteGroup.getChannelId()).getMessageById(voteGroup.getMessageId()).queue(message -> {
                        message.delete().queue();
                    });
                    //TODO this, but I need the song buttons to be merged so i can get the message and delete it.
                    Map<Object, Integer> countMap = new HashMap<>();
                    int maxCount = 0;
                    Object maxObject = null;
                    for (Map.Entry<Long, Object> entry : voteMap.entrySet()) {
                        if (countMap.containsKey(entry.getValue())) {
                            int value = countMap.get(entry.getValue()) + 1;
                            countMap.put(entry.getValue(), value);
                            if (value > maxCount) {
                                maxObject = entry.getValue();
                            }
                        } else {
                            countMap.put(entry.getValue(), 1);
                            if (1 > maxCount) {
                                maxCount = 1;
                                maxObject = entry.getValue();
                            }
                        }
                    } //TODO account for ties
                    finishConsumer.accept(maxObject);
                }
            }, voteTime);
        }
    }
}
