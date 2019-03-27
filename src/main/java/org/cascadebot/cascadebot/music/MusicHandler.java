/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.core.entities.TextChannel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicHandler {

    protected static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    private Pattern typePattern = Pattern.compile("youtube#([A-z]+)");

    private CascadeBot instance;

    public MusicHandler(CascadeBot instance) {
        this.instance = instance;
    }

    JsonParser musicJsonParser = new JsonParser();

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
        return GuildDataManager.getGuildData(guildId).getMusicPlayer();
    }

    /**
     * Searches for a list of 5 tracks and if it errors send the error to the specified channel
     *
     * @param search The string to search
     * @param channel The {@link TextChannel} to send any errors to
     * @return The list of tracks that where found
     */
    public void searchTracks(String search, TextChannel channel, Consumer<List<SearchResult>> searchResultConsumer) {
        Request request = new Request.Builder().url("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + URLEncoder.encode(search, StandardCharsets.UTF_8) + "&key=" + Config.INS.getYoutubeKey() + "&maxResults=5&type=video,playlist").build();
        CascadeBot.INS.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Messaging.sendExceptionMessage(channel, "Error searching from youtube", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if(response.body() == null) {
                    Messaging.sendDangerMessage(channel, "Youtube didn't return any data!");
                    return;
                }
                try {
                    List<SearchResult> searchResults = new ArrayList<>();
                    JsonObject json = musicJsonParser.parse(response.body().string()).getAsJsonObject();
                    JsonArray items = json.getAsJsonArray("items");
                    for(JsonElement elm : items) {
                        JsonObject item = elm.getAsJsonObject();
                        JsonObject idElm = item.getAsJsonObject("id");
                        String type = idElm.get("kind").getAsString();
                        Matcher matcher = typePattern.matcher(type);
                        if(!matcher.matches()) {
                            break;
                        }
                        type = matcher.group(1);
                        String url = "";
                        SearchResultType searchResultType = null;
                        switch (type) {
                            case "playlist":
                                searchResultType = SearchResultType.PLAYLIST;
                                url = "https://www.youtube.com/playlist?list=" + URLEncoder.encode(idElm.get("playlistId").getAsString(), StandardCharsets.UTF_8);
                                break;
                            case "video":
                                searchResultType = SearchResultType.VIDEO;
                                url = "https://www.youtube.com/watch?v=" + URLEncoder.encode(idElm.get("videoId").getAsString(), StandardCharsets.UTF_8);
                                break;
                        }
                        JsonObject snippetElm = item.getAsJsonObject("snippet");
                        String title = snippetElm.get("title").getAsString();
                        searchResults.add(new SearchResult(searchResultType, url, title));
                    }
                    searchResultConsumer.accept(searchResults);
                } catch (IOException e) {
                    Messaging.sendExceptionMessage(channel, "Error reading YouTube data!", e);
                }
            }
        });
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

    public static class SearchResult {

        private SearchResultType type;
        private String url;
        private String title;

        public SearchResult(SearchResultType type, String url, String title) {
            this.type = type;
            this.url = url;
            this.title = title;
        }

        public SearchResultType getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }
    }

    public enum SearchResultType {
        VIDEO,
        PLAYLIST
    }

    static JdaLavalink getLavaLink() {
        return lavalink;
    }

    public static boolean isLavalinkEnabled() {
        return lavalinkEnabled;
    }

}
