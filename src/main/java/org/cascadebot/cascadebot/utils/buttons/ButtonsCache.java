/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.buttons;

import org.cascadebot.cascadebot.utils.DiscordUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ButtonsCache extends HashMap<Long, LinkedHashMap<Long, ButtonGroup>> { //Long 1 is channel id. long 2 is message id.

    private int maxSize;

    public ButtonsCache(int maxSize) {
        this.maxSize = maxSize;
    }

    public void put(Long channelId, Long messageId, ButtonGroup buttonGroup) {
        if (containsKey(channelId) && get(channelId) != null) {
            get(channelId).put(messageId, buttonGroup);
        } else {
            put(channelId, new LinkedHashMap<>() {
                @Override
                protected boolean removeEldestEntry(final Map.Entry<Long, ButtonGroup> eldest) {
                    if (size() > maxSize) {
                        DiscordUtils.getTextChannelById(channelId).retrieveMessageById(eldest.getKey()).queue(buttonedMessage -> buttonedMessage.clearReactions().queue());
                        return true;
                    }
                    return false;
                }
            });
            get(channelId).put(messageId, buttonGroup);
        }
    }

    public boolean remove(Long channelId, Long messageId) {
        if (containsKey(channelId)) {
            return get(channelId).remove(messageId) != null; // Only return true if there was a previous mapping for the key
        } else {
            return false;
        }
    }

}
