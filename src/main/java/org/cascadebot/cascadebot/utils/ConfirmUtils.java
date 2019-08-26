/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.tasks.Task;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

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
        boolean useEmbed = guildData.getCoreSettings().isUseEmbedForMessages();
        Message sentMessage;
        try {
            sentMessage = Messaging.sendMessageTypeMessage(channel, type, message, useEmbed).get();
            action.userId = userId;
            action.message = sentMessage;
            confirmedMap.put(actionKey, action);
        } catch (ExecutionException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        if (sentMessage == null) {
            return false;
        }

        if (channel.getGuild().getMember(CascadeBot.INS.getSelfUser()).hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
            Task.getScheduler().schedule(() -> {
                ButtonGroup group = new ButtonGroup(userId, channel.getIdLong(), channel.getGuild().getIdLong());
                group.addButton(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel1, message1) -> {
                    if (runner.getIdLong() != action.userId) return;
                    action.run();
                }));
                group.addButtonsToMessage(sentMessage);
                group.setMessage(sentMessage.getIdLong());
                guildData.addButtonGroup(channel, sentMessage, group);
            }, buttonDelay, TimeUnit.MILLISECONDS);
        }

        Task.getScheduler().schedule(() -> {
            confirmedMap.remove(actionKey, action);
            sentMessage.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
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
        return confirmedMap.entries().stream().anyMatch(entry -> entry.getKey().equals(actionKey) && entry.getValue().userId == userId);
    }

    public static void completeAction(String actionKey, long userId) {
        Optional<Map.Entry<String, ConfirmRunnable>> entryOptional = confirmedMap.entries().stream().filter(entry -> entry.getKey().equals(actionKey) && entry.getValue().userId == userId).findFirst();
        entryOptional.ifPresent(stringConfirmRunnableEntry -> stringConfirmRunnableEntry.getValue().run());
    }

    public abstract static class ConfirmRunnable implements Runnable {

        private long userId;
        private Message message;

        @Override
        public void run() {
            message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
            execute();
        }

        public abstract void execute();

    }

}
