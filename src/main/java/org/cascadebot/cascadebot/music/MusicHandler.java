/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.io.jda.JdaLavalink;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;

import java.net.URI;

public class MusicHandler {

    private static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    private CascadeBot instance;

    public MusicHandler(CascadeBot instance) {
        this.instance = instance;
    }

    private static JdaLavalink lavalink;
    private static boolean lavalinkEnabled;

    public JdaLavalink buildMusic() {
        AudioSourceManagers.registerRemoteSources(playerManager);
        lavalink = new JdaLavalink(
                Config.INS.getBotID().toString(),
                Config.INS.getShardNum(),
                shardId -> instance.getShardManager().getShardById(shardId));

        if (Config.INS.getMusicNodes().size() > 0) {
            for (MusicNode musicNode : Config.INS.getMusicNodes()) {
                lavalink.addNode(musicNode.uri, musicNode.password); //TODO give nodes a name
            }
            lavalinkEnabled = true;
        } else {
            lavalinkEnabled = false;
        }
        return lavalink;
    }

    public CascadePlayer getPlayer(Long guildId) {
        return CascadePlayer.getCascadePlayer(guildId);
    }

    public AudioTrack getTrack(String search) {

        return null;
    }

    public static AudioPlayer createLavaLinkPlayer() {
        return playerManager.createPlayer();
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

    static JdaLavalink getLavaLink() {
        return lavalink;
    }

    public static boolean isLavalinkEnabled() {
        return lavalinkEnabled;
    }

}
