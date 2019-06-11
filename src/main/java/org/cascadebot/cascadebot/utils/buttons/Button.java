/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.buttons;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Message;
import org.cascadebot.cascadebot.Cascade;

public abstract class Button {

    IButtonRunnable runnable;

    private Button(IButtonRunnable runnable) {
        this.runnable = runnable;
    }

    public abstract void addReaction(Message message);

    public static class EmoteButton extends Button {

        @Getter
        private Long emoteId;

        public EmoteButton(Long emoteId, IButtonRunnable runnable) {
            super(runnable);
            this.emoteId = emoteId;
            if (emoteId == null || emoteId <= 0)
                Cascade.LOGGER.warn("An emote button has been registered with an invalid ID!");
        }

        @Override
        public void addReaction(Message message) {
            if (emoteId != null && Cascade.INS.getShardManager().getEmoteById(emoteId) != null) {
                message.addReaction(Cascade.INS.getShardManager().getEmoteById(emoteId)).queue();
            } else {
                Cascade.LOGGER.warn("An emote button has an invalid emote ID, please update it! ID: {}", emoteId);
            }
        }

    }

    public static class UnicodeButton extends Button {

        @Getter
        private String unicode;

        public UnicodeButton(String unicode, IButtonRunnable runnable) {
            super(runnable);
            this.unicode = unicode;
        }

        @Override
        public void addReaction(Message message) {
            message.addReaction(unicode).queue(null, error -> Cascade.LOGGER.debug("Failed to add reaction!", error));
        }

    }

}
