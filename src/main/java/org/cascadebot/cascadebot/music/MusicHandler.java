/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import lavalink.client.io.jda.JdaLavalink;
import lavalink.client.io.jda.JdaLink;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.events.PlayerListener;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.utils.PasteUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicHandler {

    @Getter
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    private Pattern typePattern = Pattern.compile("youtube#([A-z]+)");

    private CascadeBot instance;

    @Getter
    private String youtubeSourceName;
    @Getter
    private String twitchSourceName;

    @Getter
    private Map<Long, CascadePlayer> players = new HashMap<>();

    public MusicHandler(CascadeBot instance) {
        this.instance = instance;
    }

    JsonParser musicJsonParser = new JsonParser();

    private JdaLavalink lavalink;
    @Getter
    private boolean lavalinkEnabled;

    public void buildMusic() {
        AudioSourceManagers.registerRemoteSources(playerManager);

        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager(false);
        youtubeAudioSourceManager.configureRequests(config -> RequestConfig.copy(config).setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(5000).build());
        youtubeSourceName = youtubeAudioSourceManager.getSourceName();

        playerManager.registerSourceManager(youtubeAudioSourceManager);

        TwitchStreamAudioSourceManager twitchAudioManager = new TwitchStreamAudioSourceManager();
        twitchSourceName = twitchAudioManager.getSourceName();

        playerManager.registerSourceManager(twitchAudioManager);

        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());

        if (Config.INS.getMusicNodes().size() > 0) {
            lavalink = new JdaLavalink(Config.INS.getBotId().toString(), Config.INS.getShardNum(), shardId -> instance.getShardManager().getShardById(shardId));
            for (MusicNode musicNode : Config.INS.getMusicNodes()) {
                lavalink.addNode(musicNode.uri, musicNode.password); //TODO give nodes a name
            }
            lavalinkEnabled = true;
        } else {
            lavalinkEnabled = false;
        }

    }

    public CascadePlayer getPlayer(long guildId) {
        return players.computeIfAbsent(guildId, id -> {
            Guild guild = CascadeBot.INS.getShardManager().getGuildById(id);
            if (guild != null) {
                return createPlayer(guild);
            } else {
                return null;
            }
        });
    }

    private CascadePlayer createPlayer(Guild guild) {
        CascadePlayer player;
        if (isLavalinkEnabled()) {
            JdaLink link = getLavaLink().getLink(guild);
            player = new CascadeLavalinkPlayer(link.getPlayer());
        } else {
            AudioPlayer aPlayer = createLavaplayerPlayer();
            player = new CascadeLavaplayerPlayer(aPlayer);
            guild.getAudioManager().setSendingHandler(new LavaPlayerAudioSendHandler(aPlayer));
        }
        player.setGuild(guild);
        player.addListener(new PlayerListener(player));
        return player;
    }

    public boolean removePlayer(long guildId) {
        return players.remove(guildId) != null;
    }

    public void purgeDisconnectedPlayers() {
        players.entrySet().removeIf(entry -> entry.getValue().getConnectedChannel() == null
                && !GuildDataManager.getGuildData(entry.getKey()).getGuildTier().hasFlag("music_stay")
        );
    }

    /**
     * Searches for a list of 5 tracks and if it errors send the error to the specified channel
     *
     * @param search  The string to search
     * @param channel The {@link TextChannel} to send any errors to
     * @return The list of tracks that where found
     */
    public void searchTracks(String search, TextChannel channel, Consumer<List<SearchResult>> searchResultConsumer) {
        if (StringUtils.isBlank(Config.INS.getYoutubeKey())) {
            // TODO: Some way to disable searching?
            return;
        }
        Request request = new Request.Builder().url("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + URLEncoder.encode(search, StandardCharsets.UTF_8) + "&key=" + URLEncoder.encode(Config.INS.getYoutubeKey(), StandardCharsets.UTF_8) + "&maxResults=5&type=video,playlist").build();
        CascadeBot.INS.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Messaging.sendExceptionMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "music.handler.error_searching"), e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.body() == null) {
                    Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "music.handler.no_data_returned"));
                    return;
                }

                try {
                    if (!response.isSuccessful()) {
                        Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "music.handler.query_unsuccessful", PasteUtils.paste(response.body().string())));
                        return;
                    }
                    List<SearchResult> searchResults = new ArrayList<>();
                    JsonObject json = musicJsonParser.parse(response.body().string()).getAsJsonObject();
                    JsonArray items = json.getAsJsonArray("items");
                    int i = 0;
                    for (JsonElement elm : items) {
                        i++;
                        if (i > 5) {
                            CascadeBot.LOGGER.warn(Language.i18n(channel.getGuild().getIdLong(), "music.handler.more_than_five"));
                            break;
                        }
                        JsonObject item = elm.getAsJsonObject();
                        JsonObject idElm = item.getAsJsonObject("id");
                        String type = idElm.get("kind").getAsString();
                        Matcher matcher = typePattern.matcher(type);
                        if (!matcher.matches()) {
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
                    Messaging.sendExceptionMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "music.handler.error_reading"), e);
                } catch (Exception e) {
                    Messaging.sendExceptionMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "music.handler.error_processing"), e);
                }
            }
        });
    }

    private AudioPlayer createLavaplayerPlayer() {
        return playerManager.createPlayer();
    }

    public JdaLavalink getLavalink() {
        return lavalink;
    }

    @AllArgsConstructor
    @Getter
    public static class MusicNode {

        URI uri;
        String password;

        //TODO maybe add port option?

    }

    @AllArgsConstructor
    @Getter
    public static class SearchResult {

        private SearchResultType type;
        private String url;
        private String title;

    }

    public enum SearchResultType {
        VIDEO, PLAYLIST
    }

    public JdaLavalink getLavaLink() {
        return lavalink;
    }

}
