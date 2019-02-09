/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.messaging.MessageType;
import com.cascadebot.cascadebot.messaging.Messaging;
import com.cascadebot.cascadebot.tasks.Task;
import com.cascadebot.cascadebot.utils.buttons.Button;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ConfirmUtils {

    // Holds the users that have confirmed an action
    private static ListMultimap<String, ConfirmRunnable> confirmedMap = ArrayListMultimap.create();

    public static boolean confirmAction(long userId, String actionKey, TextChannel channel, MessageType type, String message, long buttonDelay, long expiry, ConfirmRunnable action) {
        GuildData guildData = GuildDataMapper.getGuildData(channel.getGuild().getIdLong());
        boolean useEmbed = guildData.getUseEmbedForMessages();
        Message sentMessage;
        try {
            sentMessage = Messaging.sendMessageTypeMessage(channel, type, message, useEmbed).get();
            action.userID = userId;
            action.message = sentMessage;
            confirmedMap.put(actionKey, action);
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
        if (sentMessage == null) {
            return false;
        }

        if (channel.getGuild().getMember(CascadeBot.INS.getSelfUser()).hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
            Task.getScheduler().schedule(() -> {
                ButtonGroup group = new ButtonGroup(userId, channel.getGuild().getIdLong());
                group.addButton(new Button.UnicodeButton("\u2705" /* Tick, confirm action */, (runner, channel1, message1) -> {
                    if (runner.getUser().getIdLong() != action.userID) return;
                    action.run();
                }));
                group.addButtonsToMessage(sentMessage);
                group.setMessage(sentMessage.getIdLong());
                guildData.addButtonGroup(channel, sentMessage, group);
            }, buttonDelay, TimeUnit.MILLISECONDS);
        }

        Task.getScheduler().schedule(() -> {
            confirmedMap.remove(actionKey, action);
            sentMessage.delete().queue();
        }, expiry, TimeUnit.MILLISECONDS);

        return true;
    }

    public static void confirmAction(long userId, String actionKey, TextChannel channel, MessageType type, String message, ConfirmRunnable action) {
        confirmAction(
                userId,
                actionKey,
                channel,
                type,
                message,
                TimeUnit.SECONDS.toMillis(2),
                TimeUnit.MINUTES.toMillis(1),
                action
        );
    }

    public static void confirmAction(long userId, String actionKey, TextChannel channel, String message, ConfirmRunnable action) {
        confirmAction(
                userId,
                actionKey,
                channel,
                MessageType.WARNING,
                message,
                TimeUnit.SECONDS.toMillis(2),
                TimeUnit.MINUTES.toMillis(1),
                action
        );
    }

    public static boolean hasConfirmedAction(String actionKey, long userId) {
        return confirmedMap.entries().stream().anyMatch(entry -> entry.getKey().equals(actionKey) && entry.getValue().userID == userId);
    }

    public static void completeAction(String actionKey, long userId) {
        Optional<Map.Entry<String, ConfirmRunnable>> entryOptional = confirmedMap.entries().stream().filter(entry -> entry.getKey().equals(actionKey) && entry.getValue().userID == userId).findFirst();
        entryOptional.ifPresent(stringConfirmRunnableEntry -> stringConfirmRunnableEntry.getValue().run());
    }

    public abstract static class ConfirmRunnable implements Runnable {

        private long userID;
        private Message message;

        @Override
        public void run() {
            message.delete().queue();
            execute();
        }

        public abstract void execute();

    }

}
