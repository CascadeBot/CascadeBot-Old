/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.buttons;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cascadebot.cascadebot.CascadeBot;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ButtonGroup {

    private List<Button> buttons = new ArrayList<>();
    private final long ownerId;
    private final long channelId;
    private final long guildId;

    private long messageId = 0;

    private Runnable messageSentAction;

    public void addButton(Button button) {
        buttons.add(button);
        Guild guild = CascadeBot.INS.getShardManager().getGuildById(guildId);
        if (guild == null) return;
        TextChannel channel = guild.getTextChannelById(channelId);
        if (messageId != 0 && channel != null) {
            channel.retrieveMessageById(messageId).queue(button::addReaction);
        }
    }

    public void removeButton(Button button) {
        buttons.remove(button);
        Guild guild = CascadeBot.INS.getShardManager().getGuildById(guildId);
        if (guild == null) return;
        TextChannel channel = guild.getTextChannelById(channelId);
        if (messageId != 0 && channel != null) {
            channel.retrieveMessageById(messageId).queue(message -> {
                for (MessageReaction reaction : message.getReactions()) {
                    MessageReaction.ReactionEmote reactionEmote = reaction.getReactionEmote();
                    if (button instanceof Button.UnicodeButton && !reactionEmote.isEmote()) {
                        Button.UnicodeButton unicodeButton = (Button.UnicodeButton) button;
                        if (reactionEmote.getName().equals(unicodeButton.getUnicode())) {
                            reaction.removeReaction(CascadeBot.INS.getSelfUser()).queue();
                        }
                    } else if (button instanceof Button.EmoteButton && reactionEmote.isEmote()) {
                        Button.EmoteButton emoteButton = (Button.EmoteButton) button;
                        if (reactionEmote.getEmote().getIdLong() == emoteButton.getEmoteId()) {
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

    public void setMessageSentAction(Runnable messageSentAction) {
        this.messageSentAction = messageSentAction;
        if (messageId != 0) {
            this.messageSentAction.run();
        }
    }

    public void setMessage(long id) {
        messageId = id;
        if (messageSentAction != null) {
            messageSentAction.run();
        }
    }

    public void handleButton(Member clicker, TextChannel channel, Message buttonMessage, MessageReaction.ReactionEmote emote) {
        for (Button button : buttons) {
            if (button instanceof Button.EmoteButton && emote.isEmote()) {
                if (((Button.EmoteButton) button).getEmoteId() == emote.getEmote().getIdLong()) {
                    button.runnable.run(clicker, channel, buttonMessage);
                    return;
                }
            } else if (button instanceof Button.UnicodeButton && !emote.isEmote()) {
                if (((Button.UnicodeButton) button).getUnicode().equals(emote.getName())) {
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
        setMessage(message.getIdLong());
    }


}
