/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.cascadebot.cascade.Cascade;
import org.cascadebot.cascade.UnicodeConstants;
import org.cascadebot.cascade.data.managers.GuildDataManager;
import org.cascadebot.cascade.data.objects.GuildData;
import org.cascadebot.cascade.messaging.MessageType;
import org.cascadebot.cascade.messaging.Messaging;
import org.cascadebot.cascade.tasks.Task;
import org.cascadebot.cascade.utils.buttons.Button;
import org.cascadebot.cascade.utils.buttons.ButtonGroup;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class ConfirmUtils {

    // Holds the users that have confirmed an action
    private static ListMultimap<String, ConfirmRunnable> confirmedMap = ArrayListMultimap.create();

    //region Confirm Action
    public static boolean confirmAction(long userId, String actionKey, TextChannel channel, MessageType type, String message, long buttonDelay, long expiry, ConfirmRunnable action) {
        GuildData guildData = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
        boolean useEmbed = guildData.getSettings().isUseEmbedForMessages();
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

        if (channel.getGuild().getMember(Cascade.INS.getSelfUser()).hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
            Task.getScheduler().schedule(() -> {
                ButtonGroup group = new ButtonGroup(userId, channel.getIdLong(), channel.getGuild().getIdLong());
                group.addButton(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel1, message1) -> {
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
    //endregion

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
