/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.music;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.Config;
import lavalink.client.io.jda.JdaLavalink;

import java.net.URI;

public class MusicHandler {

    CascadeBot instance;
    public MusicHandler(CascadeBot instance) {
        this.instance = instance;
    }

    private JdaLavalink lavalink;
    private boolean lavalinkEnabled;

    public void buildMusic() {
        lavalink = new JdaLavalink(
                Config.INS.getBotID().toString(),
                Config.INS.getSharNum(),
                shardId -> instance.getShardManager().getShardById(shardId));

        if(Config.INS.getMusicNodes().size() > 0) {
            for (MusicNode musicNode : Config.INS.getMusicNodes()) {
                lavalink.addNode(musicNode.uri, musicNode.password); //TODO give nodes a name
            }
            lavalinkEnabled = true;
        } else {
            lavalinkEnabled = false;
        }
    }

    public static class MusicNode {

        URI uri;

        String password;

        //TODO maybe add port option?

        public MusicNode(URI uri, String password) {
            this.uri = uri;
            this.password = password;
        }
    }
}
