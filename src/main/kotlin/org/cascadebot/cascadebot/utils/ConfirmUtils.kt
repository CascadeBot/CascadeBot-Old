/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.requests.ErrorResponse
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.Messaging.sendMessage
import org.cascadebot.cascadebot.tasks.Task
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow
import org.cascadebot.cascadebot.utils.interactions.CascadeButton
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer
import org.cascadebot.cascadebot.utils.interactions.IButtonRunnable
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

private typealias ActionKey = String

object ConfirmUtils {

    // Holds the users that have confirmed an action
    // Map is in the format ActionKey as the key and the list of conformation actions
    @JvmStatic
    private val confirmedMap: ListMultimap<ActionKey, ConfirmationAction> = ArrayListMultimap.create()

    /**
     * Registers an action to be confirmed. This method sends the message specified from the input and
     * adds a ✅ button to it (And also a ❌ button if `isCancellable` is true). If the user clicks the
     * check button before the expiry time, the action is run and the original confimration message is deleted.
     *
     * @param userId The ID of the user this action is associated with. The action can only be run by this user.
     * @param actionKey An internal identified for this action. Should be a short string with lowercase and hyphens
     * only (Although this is not a technical restriction, just a stylistic one)
     * @param channel The channel in which to send the confirmation message.
     * @param type What message type the message should be displayed as. This only affects the color of the message.
     * @param message The confirmation message to send to the user.
     * @param buttonDelay How long between the message sending and the buttons being added. This is useful if you
     * want to make sure the user has read the confirmation.
     * @param expiry How long the confirmation action is valid for. After this length of time, the action will not be run.
     * @param isCancellable Whether the ❌ button will appear to cancel the message.
     * @param action The action to run **only** if the user confirms the action before the expiry time is up.
     *
     * @return Whether the confirmation action has been successfully registered.
     */
    @JvmStatic
    @JvmOverloads
    fun registerForConfirmation(
            userId: Long,
            actionKey: String,
            channel: TextChannel,
            type: MessageType = MessageType.WARNING,
            message: String,
            buttonDelay: Long = TimeUnit.SECONDS.toMillis(2),
            expiry: Long = TimeUnit.MINUTES.toMillis(1),
            isCancellable: Boolean,
            action: Runnable
    ): Boolean {
        //val guildData = GuildDataManager.getGuildData(channel.guild.idLong)
        val confirmationAction: ConfirmationAction
        //val useEmbed = guildData.core.useEmbedForMessages
        val sentMessage: Message
        try {
            sentMessage = sendMessage(type, channel, message, true).get()
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
                val container = ComponentContainer()
                val row = CascadeActionRow()
                container.addRow(row)

                row.addComponent(CascadeButton.success(Emoji.fromUnicode(UnicodeConstants.TICK), IButtonRunnable { member, _, _ ->
                    if (member.idLong != confirmationAction.userId)
                        confirmationAction.run()
                }))

                if (isCancellable) {
                    row.addComponent(CascadeButton.danger(Emoji.fromUnicode(UnicodeConstants.RED_CROSS), IButtonRunnable { _, _, _ ->
                        confirmedMap.remove(actionKey, confirmationAction)
                        sentMessage.delete()
                            .queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
                    }))
                }

                sentMessage.editMessageComponents(container.getComponents().map { it.toDiscordActionRow() }).override(true).queue()

                //guildData.addComponents(channel, sentMessage, container)
            }, buttonDelay, TimeUnit.MILLISECONDS)
        }
        Task.getScheduler().schedule({
            confirmedMap.remove(actionKey, confirmationAction)
            sentMessage.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
        }, expiry, TimeUnit.MILLISECONDS)
        return true
    }

    /**
     * Checks whether a user has an action registered under the specified key.
     *
     * @param actionKey The key of the action to check for.
     * @param userId The ID of the user for which we are searching for actions.
     * @return Whether an action matching the criteria is found in the internal map.
     */
    @JvmStatic
    fun hasRegisteredAction(actionKey: String, userId: Long): Boolean {
        return confirmedMap.entries().any { it.key == actionKey && it.value.userId == userId }
    }

    /**
     * Manually confirms an action the user has registered. Useful for when reactions are not enabled in a guild.
     *
     * If multiple actions exist for this user, only the first is run.
     *
     * If no actions exist, this method returns false.
     *
     * @param actionKey The action key for the action to confirm.
     * @param userId The user associated with the action.
     * @return `true` if the action has been run, `false` is no action exists with this criteria.
     */
    @JvmStatic
    fun confirmAction(actionKey: String, userId: Long): Boolean {
        val entryOptional = confirmedMap.entries().firstOrNull { it.key == actionKey && it.value.userId == userId }
        return if (entryOptional != null) {
            entryOptional.value.run()
            true
        } else {
            false
        }
    }

    private class ConfirmationAction(val userId: Long, val message: Message, private val runnable: Runnable) {
        fun run() {
            message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE))
            runnable.run()
        }
    }
}