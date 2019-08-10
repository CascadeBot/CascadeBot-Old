/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EventWaiter extends com.jagrosh.jdautilities.commons.waiter.EventWaiter {

    public void waitForResponse(User user, TextChannel channel, TextResponse... responses) {
        waitForResponse(user, channel, -1, null, null, responses);
    }

    public void waitForResponse(User user, TextChannel channel, long timeout, TimeUnit unit, Runnable timeoutAction, TextResponse... responses) {
        Map<String, TextResponse> responseMap = new HashMap<>();
        for (TextResponse response : responses) {
            for (String expectedResponse : response.getResponses()) {
                responseMap.put(expectedResponse.toLowerCase(), response);
            }
        }
        waitForEvent(GuildMessageReceivedEvent.class, event -> {
            return event.getChannel().getIdLong() == channel.getIdLong() &&
                    event.getAuthor().getIdLong() == user.getIdLong() &&
                    responseMap.containsKey(event.getMessage().getContentRaw().toLowerCase());
        }, event -> {
            responseMap.get(event.getMessage().getContentRaw().toLowerCase()).getAction().accept(event);
        }, timeout, unit, timeoutAction);
    }


    public static class TextResponse {

        private final String[] responses;
        private final Consumer<GuildMessageReceivedEvent> action;

        public TextResponse(Consumer<GuildMessageReceivedEvent> action, String response) {
            this.responses = new String[]{response};
            this.action = action;
        }

        public TextResponse(Consumer<GuildMessageReceivedEvent> action, String... responses) {
            this.responses = responses;
            this.action = action;
        }

        public String[] getResponses() {
            return responses;
        }

        public Consumer<GuildMessageReceivedEvent> getAction() {
            return action;
        }

    }

}
