/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.metrics.Metrics;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.buttons.ButtonsCache;

import java.util.Objects;

public class ButtonEventListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (Objects.equals(e.getMember(), e.getGuild().getSelfMember())) {
            return;
        }
        if (e.getChannel().getType().equals(ChannelType.TEXT)) {
            TextChannel channel = (TextChannel) e.getChannel();
            GuildData data = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
            ButtonsCache cache = data.getButtonsCache();
            if (cache.containsKey(channel.getIdLong())) {
                if (cache.get(channel.getIdLong()).containsKey(e.getMessageIdLong())) {
                    ButtonGroup group = cache.get(channel.getIdLong()).get(e.getMessageIdLong());
                    Metrics.INS.buttonsPressed.labels(e.getReaction().getReactionEmote().getName()).inc();
                    e.getChannel().retrieveMessageById(e.getMessageId()).queue(message -> group.handleButton(e.getMember(), channel, message, e.getReactionEmote()));
                    e.getReaction().removeReaction(e.getMember().getUser()).queue(); //Idk if we want to allow other reactions on the message
                    //TODO perms checking
                }
            }
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent e) {
        if (e.getChannel().getType().equals(ChannelType.TEXT)) {
            TextChannel channel = (TextChannel) e.getChannel();
            GuildData data = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
            ButtonsCache cache = data.getButtonsCache();
            if (cache.containsKey(channel.getIdLong())) {
                cache.get(channel.getIdLong()).remove(e.getMessageIdLong());
            }
        }
    }

}
