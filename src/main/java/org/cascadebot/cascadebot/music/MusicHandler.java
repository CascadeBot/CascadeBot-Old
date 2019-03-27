/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.io.jda.JdaLavalink;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.mapping.GuildDataMapper;

import java.io.IOException;
import java.net.URI;
import java.util.List;

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

        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager(false);
        youtubeAudioSourceManager.configureRequests(config -> RequestConfig.copy(config).setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(5000).build());

        playerManager.registerSourceManager(youtubeAudioSourceManager);

        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());

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
        return GuildDataMapper.getGuildData(guildId).getMusicPlayer();
    }

    public List<AudioTrack> searchTracks(String search) { //TODO this
        Request request = new Request.Builder().url("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + search + "&key=" + Config.INS.getYoutubeKey() + "&maxResults=5").build();
        CascadeBot.INS.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {

            }
        });
        return null;
    }

    public AudioTrack getTrack(String url) {

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
