/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events

import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.metrics.Metrics
import org.cascadebot.cascadebot.utils.interactions.CascadeButton
import org.cascadebot.cascadebot.utils.interactions.CascadeSelectBox
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer.Companion.fromDiscordObjects
import org.cascadebot.cascadebot.utils.interactions.InteractionMessage

class ButtonEventListener : ListenerAdapter() {

    override fun onButtonClick(event: ButtonClickEvent) {
        if (!event.isFromGuild) return

        val container = getContainerFromEvent(event) ?: return

        val button = container.getComponents()
            .flatMap { actionRow -> actionRow.getComponents() }
            .filterIsInstance<CascadeButton>()
            .firstOrNull { component -> component.id == event.componentId }

        if (button == null) {
            // this should not be possible because as long as the container is in the cache, it will be able to find the button as the button is on the message that the container applies to.
            CascadeBot.LOGGER.error("Button was null when it should not be able to be null! Something is broken! Maybe race condition?")
            return
        }

        event.deferEdit().queue {
            button.consumer(event.member!!, TODO("owner?"), event.textChannel, InteractionMessage(
                    event.message!!, container
                )
            )
            Metrics.INS.buttonsPressed.labels(button.id, "button").inc()
        }
    }

    override fun onSelectionMenu(event: SelectionMenuEvent) {
        if (!event.isFromGuild) return

        val container = getContainerFromEvent(event) ?: return

        val select = container.getComponents()
            .flatMap { row -> row.getComponents() }
            .filterIsInstance<CascadeSelectBox>()
            .firstOrNull { selectBox: CascadeSelectBox -> selectBox.id == event.componentId }

        if (select == null) {
            // this should not be possible because as long as the container is in the cache, it will be able to find the button as the button is on the message that the container applies to.
            CascadeBot.LOGGER.error("Select box was null when it should not be able to be null! Something is broken! Maybe race condition?")
            return
        }

        event.deferEdit().queue {
            select.consumer.invoke(
                event.member!!, TODO("Owner"), event.textChannel, InteractionMessage(
                    event.message!!, container
                ), event.values
            )
            Metrics.INS.buttonsPressed.labels(select.id, "select").inc()
        }
    }

    private fun getContainerFromEvent(event: GenericComponentInteractionCreateEvent): ComponentContainer? {
        if (event.channel.type == ChannelType.TEXT) {
            val channel = event.channel as TextChannel
            val message = channel.retrieveMessageById(event.messageIdLong).complete()
            return fromDiscordObjects(event.channel.idLong, message.actionRows)
        }
        return null
    }

    override fun onMessageDelete(e: MessageDeleteEvent) {
        if (e.channel.type == ChannelType.TEXT) {
            val channel = e.channel as TextChannel
            /*GuildData data = GuildDataManager.getGuildData(channel.getGuild().getIdLong());
            InteractionCache cache = data.getComponentCache();
            if (cache.containsKey(channel.getIdLong())) {
                cache.get(channel.getIdLong()).remove(e.getMessageIdLong());
            }*/
        }
    }
}
