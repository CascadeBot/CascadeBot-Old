/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.music;

import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;

import java.util.HashMap;
import java.util.Map;

public class CascadePlayer {

    private static Map<Long, CascadePlayer> playerMap = new HashMap<>();

    private IPlayer player;

    private CascadePlayer(Long guildId) {
        player = MusicHandler.isLavalinkEnabled() ?
                MusicHandler.getLavaLink().getLink(guildId.toString()).getPlayer() :
                new LavaplayerPlayerWrapper(MusicHandler.createLavaLinkPlayer());
    }

    public static CascadePlayer getCascadePlayer(Long guildId) {
        return playerMap.computeIfAbsent(guildId, CascadePlayer::new);
    }

    //TODO implement player methods
}
