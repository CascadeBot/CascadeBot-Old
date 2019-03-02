/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils.buttons;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;

public abstract class Button {

    IButtonRunnable runnable;

    private Button(IButtonRunnable runnable) {
        this.runnable = runnable;
    }

    public abstract void addReaction(Message message);

    public static class EmoteButton extends Button {

        Emote emote;

        public EmoteButton(Emote emote, IButtonRunnable runnable) {
            super(runnable);
            this.emote = emote;
        }

        @Override
        public void addReaction(Message message) {
            message.addReaction(emote).queue();
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
            message.addReaction(unicode).queue();
        }

    }

}
