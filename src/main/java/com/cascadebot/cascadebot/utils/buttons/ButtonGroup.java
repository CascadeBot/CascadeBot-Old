/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils.buttons;

import com.cascadebot.cascadebot.CascadeBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class ButtonGroup {

    private List<Button> buttons;
    private long ownerId;
    private long guildId;

    private long messageId;

    public ButtonGroup(long ownerId, long guildId) {
        buttons = new ArrayList<>();
        this.ownerId = ownerId;
        this.guildId = guildId;
    }

    public void addButton(Button button) {
        buttons.add(button);
    }

    public Member getOwner() {
        return CascadeBot.INS.getShardManager().getGuildById(guildId).getMemberById(ownerId);
    }

    public void setMessage(long id) {
        messageId = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void handleButton(Member clicker, TextChannel channel, Message buttonMessage, MessageReaction.ReactionEmote emote) {
        for (Button button : buttons) {
            if (button instanceof Button.EmoteButton && emote.isEmote()) {
                if (((Button.EmoteButton) button).emote.equals(emote.getEmote())) {
                    button.runnable.run(clicker, channel, buttonMessage);
                    return;
                }
            } else if (button instanceof Button.UnicodeButton && !emote.isEmote()) {
                if (((Button.UnicodeButton) button).unicode.equals(emote.getName())) {
                    button.runnable.run(clicker, channel, buttonMessage);
                    return;
                }
            }
        }
    }

    public void addButtonsToMessage(Message message) {
        if (buttons == null) return;
        for (Button button : buttons) {
            button.addReaction(message);
        }
    }


}
