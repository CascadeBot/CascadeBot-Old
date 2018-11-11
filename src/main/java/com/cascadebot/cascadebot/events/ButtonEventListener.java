/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.cascadebot.cascadebot.utils.buttons.ButtonsCache;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ButtonEventListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if(e.getMember().equals(e.getGuild().getSelfMember())) {
            return;
        }
        if(e.getChannel().getType().equals(ChannelType.TEXT)) {
            TextChannel channel = (TextChannel) e.getChannel();
            GuildData data = GuildData.getGuildData(channel.getGuild().getIdLong());
            ButtonsCache cache = data.getButtonsCache();
            if(cache.containsKey(channel.getIdLong())) {
                if(cache.get(channel.getIdLong()).containsKey(e.getMessageIdLong())) {
                    ButtonGroup group = cache.get(channel.getIdLong()).get(e.getMessageIdLong());
                    e.getChannel().getMessageById(e.getMessageId()).queue(message -> group.hanndleButton(e.getMember(), channel, message, e.getReactionEmote()));
                    e.getReaction().removeReaction(e.getMember().getUser()).queue(); //Idk if we want to allow other reactions on the message
                    //TODO perms checking
                }
            }
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent e) {
        if(e.getChannel().getType().equals(ChannelType.TEXT)) {
            TextChannel channel = (TextChannel) e.getChannel();
            GuildData data = GuildData.getGuildData(channel.getGuild().getIdLong());
            ButtonsCache cache = data.getButtonsCache();
            if (cache.containsKey(channel.getIdLong())) {
                cache.get(channel.getIdLong()).remove(e.getMessageIdLong());
            }
        }
    }
}
