/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.buttons;

import net.dv8tion.jda.core.entities.Message;
import org.cascadebot.cascadebot.CascadeBot;

public abstract class Button {

    IButtonRunnable runnable;

    private Button(IButtonRunnable runnable) {
        this.runnable = runnable;
    }

    public abstract void addReaction(Message message);

    public static class EmoteButton extends Button {

        private Long emoteId;

        public EmoteButton(Long emoteId, IButtonRunnable runnable) {
            super(runnable);
            this.emoteId = emoteId;
        }

        @Override
        public void addReaction(Message message) {
            if (emoteId != null && CascadeBot.INS.getShardManager().getEmoteById(emoteId) != null) {
                message.addReaction(CascadeBot.INS.getShardManager().getEmoteById(emoteId)).queue();
            }
        }

    }

    public static class UnicodeButton extends Button {

        String unicode;

        public UnicodeButton(String unicode, IButtonRunnable runnable) {
            super(runnable);
            this.unicode = unicode;
        }

        @Override
        public void addReaction(Message message) {
            message.addReaction(unicode).queue(null, error -> CascadeBot.LOGGER.debug("Failed to add reaction!", error));
        }

    }

}
