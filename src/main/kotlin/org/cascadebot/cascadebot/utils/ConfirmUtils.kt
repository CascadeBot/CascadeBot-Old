/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.requests.ErrorResponse
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.Messaging.sendMessage
import org.cascadebot.cascadebot.tasks.Task
import org.cascadebot.cascadebot.utils.buttons.Button.UnicodeButton
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup
import org.cascadebot.cascadebot.utils.buttons.IButtonRunnable
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

object ConfirmUtils {

    // Holds the users that have confirmed an action
    @JvmStatic
    private val confirmedMap: ListMultimap<String, ConfirmationAction> = ArrayListMultimap.create()

    //region Confirm Action
    @JvmStatic
    fun confirmAction(userId: Long, actionKey: String, channel: TextChannel, type: MessageType, message: String, buttonDelay: Long, expiry: Long, isCancellable: Boolean, action: Runnable): Boolean {
        val guildData = GuildDataManager.getGuildData(channel.guild.idLong)
        val confirmationAction: ConfirmationAction
        val useEmbed = guildData.core.useEmbedForMessages
        val sentMessage: Message
        try {
            sentMessage = sendMessage(type, channel, message, useEmbed).get()
            confirmationAction = ConfirmationAction(userId, sentMessage, action)
            confirmedMap.put(actionKey, confirmationAction)
        } catch (e: ExecutionException) {
            return false
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            return false
        }
        if (sentMessage == null) {
            return false
        }
        if (channel.guild.getMember(CascadeBot.INS.selfUser)!!.hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
            Task.getScheduler().schedule({
                val group = ButtonGroup(userId, channel.idLong, channel.guild.idLong)

                group.addButton(UnicodeButton(UnicodeConstants.TICK, IButtonRunnable { runner: Member, _, _ ->
                    if (runner.idLong != confirmationAction.userId) return@IButtonRunnable
                    confirmationAction.run()
                }))

                if (isCancellable) {
                    group.addButton(UnicodeButton(UnicodeConstants.RED_CROSS, IButtonRunnable { _, _, _ ->
                        confirmedMap.remove(actionKey, confirmationAction)
                        sentMessage.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
                    }))
                }
                group.addButtonsToMessage(sentMessage)
                group.setMessage(sentMessage.idLong)
                guildData.addButtonGroup(channel, sentMessage, group)
            }, buttonDelay, TimeUnit.MILLISECONDS)
        }
        Task.getScheduler().schedule({
            confirmedMap.remove(actionKey, confirmationAction)
            sentMessage.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
        }, expiry, TimeUnit.MILLISECONDS)
        return true
    }

    @JvmStatic
    fun confirmAction(userId: Long, actionKey: String, channel: TextChannel, type: MessageType, message: String, isCancellable: Boolean, action: Runnable) {
        confirmAction(
                userId,
                actionKey,
                channel,
                type,
                message,
                TimeUnit.SECONDS.toMillis(2),
                TimeUnit.MINUTES.toMillis(1),
                isCancellable,
                action
        )
    }

    @JvmStatic
    fun confirmAction(userId: Long, actionKey: String, channel: TextChannel, message: String, isCancellable: Boolean, action: Runnable) {
        confirmAction(
                userId,
                actionKey,
                channel,
                MessageType.WARNING,
                message,
                TimeUnit.SECONDS.toMillis(2),
                TimeUnit.MINUTES.toMillis(1),
                isCancellable,
                action
        )
    }

    //endregion
    @JvmStatic
    fun hasConfirmedAction(actionKey: String, userId: Long): Boolean {
        return confirmedMap.entries().stream().anyMatch { it.key == actionKey && it.value.userId == userId }
    }

    @JvmStatic
    fun completeAction(actionKey: String, userId: Long) {
        val entryOptional = confirmedMap.entries().stream().filter { it.key == actionKey && it.value.userId == userId }.findFirst()
        entryOptional.ifPresent { it.value.run() }
    }

    private class ConfirmationAction(val userId: Long, val message: Message, private val runnable: Runnable) {

        fun run() {
            message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
            runnable.run()
        }
    }
}