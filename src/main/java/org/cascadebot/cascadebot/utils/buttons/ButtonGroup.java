/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.buttons;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import org.cascadebot.cascadebot.CascadeBot;

import java.util.ArrayList;
import java.util.List;

public class ButtonGroup {

    private List<Button> buttons;
    private long ownerId;
    private long channelId;
    private long guildId;

    private long messageId = 0;

    public ButtonGroup(long ownerId, long channelId, long guildId) {
        buttons = new ArrayList<>();
        this.ownerId = ownerId;
        this.channelId = channelId;
        this.guildId = guildId;
    }

    public void addButton(Button button) {
        buttons.add(button);
        if (messageId != 0) {
            CascadeBot.INS.getShardManager().getGuildById(guildId).getTextChannelById(channelId).getMessageById(messageId).queue(button::addReaction);
        }
    }

    public void removeButton(Button button) {
        buttons.remove(button);
        if (messageId != 0) {
            CascadeBot.INS.getShardManager().getGuildById(guildId).getTextChannelById(channelId).getMessageById(messageId).queue(message -> {
                for (MessageReaction reaction : message.getReactions()) {
                    MessageReaction.ReactionEmote reactionEmote = reaction.getReactionEmote();
                    if (button instanceof Button.UnicodeButton && !reactionEmote.isEmote()) {
                        Button.UnicodeButton unicodeButton = (Button.UnicodeButton) button;
                        if (reactionEmote.getName().equals(unicodeButton.unicode)) {
                            reaction.removeReaction(CascadeBot.INS.getSelfUser()).queue();
                        }
                    } else if (button instanceof Button.EmoteButton && reactionEmote.isEmote()) {
                        Button.EmoteButton emoteButton = (Button.EmoteButton) button;
                        if (reactionEmote.getEmote().equals(emoteButton.emote)) {
                            reaction.removeReaction(CascadeBot.INS.getSelfUser()).queue();
                        }
                    }
                }
            });
        }
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

    public long getGuildId() {
        return guildId;
    }

    public void addButtonsToMessage(Message message) {
        if (buttons == null) return;
        for (Button button : buttons) {
            button.addReaction(message);
        }
    }


}
