/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.managers.PlaylistManager;
import org.cascadebot.cascadebot.data.objects.Playlist;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.events.PlayerListener;
import org.cascadebot.cascadebot.utils.StringsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class CascadePlayer {

    private Queue<AudioTrack> queue = new LinkedList<>();

    private long guildId;
    private IPlayer player;

    private LoopMode loopMode = LoopMode.DISABLED;
    private boolean shuffleEnabled = false;

    public CascadePlayer(Guild guild) {
        if (MusicHandler.isLavalinkEnabled()) {
            player = MusicHandler.getLavaLink().getLink(guild).getPlayer();
        } else {
            AudioPlayer aPlayer = MusicHandler.createLavaLinkPlayer();
            player = new LavaplayerPlayerWrapper(aPlayer);
            guild.getAudioManager().setSendingHandler(new LavaPlayerAudioSendHandler(aPlayer));
        }
        player.addListener(new PlayerListener(this));
        guildId = guild.getIdLong();
    }

    public double getQueueLength() {
        double queueLength = player.getPlayingTrack().getDuration();
        for (AudioTrack track : queue) {
            queueLength += track.getDuration();
        }
        return queueLength;
    }

    /**
     * Gets the progress bar for the current track
     *
     * @return The progress bar for the current track
     */
    public String getTrackProgressBar(boolean embed) {
        float process = (100f / player.getPlayingTrack().getDuration() * player.getTrackPosition());
        if (embed) {
            return StringsUtil.getProgressBarEmbed(process);
        } else {
            return StringsUtil.getProgressBar(process);
        }
    }

    public String getArtwork() {
        if (player.getPlayingTrack().getInfo().uri.contains("youtube")) {
            return "https://img.youtube.com/vi/" + player.getPlayingTrack().getIdentifier() + "/hqdefault.jpg";
        }
        if (player.getPlayingTrack().getInfo().uri.contains("twitch")) {
            String[] split = player.getPlayingTrack().getInfo().identifier.split("/");
            return "https://static-cdn.jtvnw.net/previews-ttv/live_user_" + split[split.length - 1] + "-500x400.jpg";
        }
        return null;
    }

    public void addTrack(AudioTrack track) {
        if (player.getPlayingTrack() != null) {
            queue.add(track);
        } else {
            player.playTrack(track);
        }
    }

    public void addTracks(Collection<AudioTrack> tracks) {
        tracks.forEach(this::addTrack);
    }

    public void loopMode(LoopMode loopMode) {
        this.loopMode = loopMode;
    }

    //Don't know if this will eb uses at all, but it's here if we want to.
    public boolean toggleShuffleOnRepeat() {
        return (shuffleEnabled = !shuffleEnabled);
    }

    public void shuffle() {
        List<AudioTrack> tracks = new ArrayList<>(getQueue());
        Collections.shuffle(tracks);
        this.queue = new LinkedList<>();
        this.queue.addAll(tracks);
    }

    public void skip() {
        player.stopTrack();
    }

    public void join(VoiceChannel channel) {
        if (MusicHandler.isLavalinkEnabled()) {
            getLink().connect(channel);
        } else {
            channel.getGuild().getAudioManager().openAudioConnection(channel);
        }
    }

    public void leave() {
        if (MusicHandler.isLavalinkEnabled()) {
            getLink().disconnect();
        } else {
            getGuild().getAudioManager().closeAudioConnection();
        }
    }

    public VoiceChannel getConnectedChannel() {
        if (MusicHandler.isLavalinkEnabled()) {
            if (getLink().getChannel() != null) {
                return CascadeBot.INS.getShardManager().getVoiceChannelById(getLink().getChannel());
            } else {
                return null;
            }
        } else {
            return getGuild().getAudioManager().getConnectedChannel();
        }
    }

    private Guild getGuild() {
        return CascadeBot.INS.getShardManager().getGuildById(guildId);
    }

    public JdaLink getLink() {
        return MusicHandler.getLavaLink().getLink(getGuild());
    }

    public void stop() {
        queue.clear();
        loopMode = LoopMode.DISABLED;
        player.stopTrack();
    }

    public void loadLink(String input, long requestUser, Consumer<String> noMatchConsumer, Consumer<FriendlyException> exceptionConsumer, Consumer<List<AudioTrack>> resultTracks) {
        MusicHandler.getPlayerManager().loadItem(input, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                audioTrack.setUserData(requestUser);
                resultTracks.accept(Collections.singletonList(audioTrack));
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = new ArrayList<>();
                for (AudioTrack track : audioPlaylist.getTracks()) {
                    track.setUserData(requestUser);
                    tracks.add(track);
                }
                resultTracks.accept(Collections.unmodifiableList(tracks));
            }

            @Override
            public void noMatches() {
                noMatchConsumer.accept(input);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                exceptionConsumer.accept(e);
            }
        });
    }

    public void loadPlaylist(String name, Member sender, BiConsumer<LoadPlaylistResult, List<AudioTrack>> consumer) {
        Playlist guild = PlaylistManager.getPlaylistByName(sender.getGuild().getIdLong(), PlaylistType.GUILD, name);
        Playlist user = PlaylistManager.getPlaylistByName(sender.getIdLong(), PlaylistType.USER, name);
        if (guild != null && user != null) {
            consumer.accept(LoadPlaylistResult.EXISTS_IN_ALL_SCOPES, null);
        } else if (guild != null) {
            loadLoadedPlaylist(guild, sender.getIdLong(), tracks -> {
                consumer.accept(LoadPlaylistResult.LOADED_GUILD, tracks);
            });
        } else if (user != null) {
            loadLoadedPlaylist(user, sender.getIdLong(), tracks -> {
                consumer.accept(LoadPlaylistResult.LOADED_USER, tracks);
            });
        } else {
            consumer.accept(LoadPlaylistResult.DOESNT_EXIST, null);
        }
    }

    public void loadPlaylist(String name, Member sender, PlaylistType scope, BiConsumer<LoadPlaylistResult, List<AudioTrack>> consumer) {
        LoadPlaylistResult result = LoadPlaylistResult.DOESNT_EXIST;
        long owner = 0;
        switch (scope) {
            case GUILD:
                result = LoadPlaylistResult.LOADED_GUILD;
                owner = sender.getGuild().getIdLong();
                break;
            case USER:
                result = LoadPlaylistResult.LOADED_USER;
                owner = sender.getIdLong();
                break;
        }
        Playlist playlist = PlaylistManager.getPlaylistByName(owner, scope, name);
        if (playlist == null) {
            consumer.accept(LoadPlaylistResult.DOESNT_EXIST, null);
            return;
        }

        LoadPlaylistResult loadPlaylistResult = result;
        loadLoadedPlaylist(playlist, sender.getIdLong(), tracks -> {
            consumer.accept(loadPlaylistResult, tracks);
        });
    }

    private void loadLoadedPlaylist(Playlist playlist, long reqUser, Consumer<List<AudioTrack>> loadedConsumer) {
        List<AudioTrack> tracks = new ArrayList<>();
        for (String url : playlist.getTracks()) {
            loadLink(url, reqUser, noMatch -> {
                playlist.removeTrack(url);
            }, exception -> {
                playlist.removeTrack(url);
            }, loadedTracks -> {
                tracks.addAll(loadedTracks);
                if (tracks.size() == playlist.getTracks().size()) {
                    loadedConsumer.accept(tracks);
                }
            });
        }
    }

    public SavePlaylistResult saveCurrentPlaylist(long owner, PlaylistType scope, String name, boolean overwrite) {
        List<AudioTrack> tracks = new ArrayList<>();
        tracks.add(player.getPlayingTrack());
        tracks.addAll(this.queue);

        List<String> ids = new ArrayList<>();
        for (AudioTrack track : tracks) {
            ids.add(track.getIdentifier());
        }

        Playlist search = PlaylistManager.getPlaylistByName(owner, scope, name);
        if (search != null) {
            if (overwrite) {
                search.setTracks(ids);
                PlaylistManager.replacePlaylist(search);
                return SavePlaylistResult.OVERWRITE;
            } else {
                return SavePlaylistResult.ALREADY_EXISTS;
            }
        } else {
            PlaylistManager.savePlaylist(new Playlist(owner, name, scope, ids));
            return SavePlaylistResult.NEW;
        }
    }

    public enum LoopMode {

        DISABLED,
        PLAYLIST,
        SONG

    }

    public enum SavePlaylistResult {

        ALREADY_EXISTS,
        OVERWRITE,
        NEW

    }

    public enum LoadPlaylistResult {

        LOADED_GUILD,
        LOADED_USER,
        EXISTS_IN_ALL_SCOPES,
        DOESNT_EXIST

    }

}
