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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
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
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Consumer;

public class CascadePlayer {

    private Queue<AudioTrack> tracks = new LinkedList<>();

    private long guildId;
    private IPlayer player;

    private LoopMode loopMode = LoopMode.DISABLED;
    private boolean shuffle = false;

    public CascadePlayer(Guild guild) {
        if(MusicHandler.isLavalinkEnabled()) {
            player = MusicHandler.getLavaLink().getLink(guild).getPlayer();
        } else {
            AudioPlayer aPlayer = MusicHandler.createLavaLinkPlayer();
            player = new LavaplayerPlayerWrapper(aPlayer);
            guild.getAudioManager().setSendingHandler(new LavaPlayerAudioSendHandler(aPlayer));
        }
        player.addListener(new PlayerListener(this));
        guildId = guild.getIdLong();
    }

    public IPlayer getPlayer() {
        return player;
    }

    public Queue<AudioTrack> getTracks() {
        return tracks;
    }

    public double getQueueLength() {
        double queueLength = player.getPlayingTrack().getDuration();
        for (AudioTrack track : tracks) {
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
        if(embed) {
            return StringsUtil.getProgressBarEmbed(process);
        } else {
            return StringsUtil.getProgressBar(process);
        }
    }

    public String getArtwork() {
        if (player.getPlayingTrack().getInfo().uri.contains("youtube")) {
            return "https://img.youtube.com/vi/" + player.getPlayingTrack().getIdentifier() + "/hqdefault.jpg";
        }
        return null;
    }

    public void addTrack(AudioTrack track) {
        if (player.getPlayingTrack() != null) {

            tracks.add(track);
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
        return (shuffle = !shuffle);
    }

    public void shuffle() {
        List<AudioTrack> tracks = new ArrayList<>(getTracks());
        Collections.shuffle(tracks);
        this.tracks = new LinkedList<>();
        this.tracks.addAll(tracks);
    }

    public LoopMode getLoopMode() {
        return this.loopMode;
    }

    public boolean isShuffleEnabled() {
        return shuffle;
    }

    public boolean skip() {
        try {
            player.playTrack(tracks.remove());
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void join(VoiceChannel channel) {
        if(MusicHandler.isLavalinkEnabled()) {
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
            return CascadeBot.INS.getShardManager().getVoiceChannelById(getLink().getChannel());
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
        tracks.clear();
        player.stopTrack();
    }

    public void loadLink(String input, Consumer<String> noMatchConsumer, Consumer<FriendlyException> exceptionConsumer, Consumer<List<AudioTrack>> resultTracks) {
        MusicHandler.playerManager.loadItem(input, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                resultTracks.accept(Collections.singletonList(audioTrack));
                addTrack(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = new ArrayList<>();
                for (AudioTrack track : audioPlaylist.getTracks()) {
                    tracks.add(track);
                    addTrack(track);
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

    public void loadPlaylist(Playlist playlist) {
        for (String url : playlist.getTracks()) {
            loadLink(url, noMatch -> {
                playlist.removeTrack(url);
            }, exception -> {
                playlist.removeTrack(url);
            }, tracks -> {

            });
        }
    }

    public void saveCurrentPlaylist(long owner, PlaylistType scope, String name) {
        List<AudioTrack> tracks = new ArrayList<>();
        tracks.add(player.getPlayingTrack());
        tracks.addAll(this.tracks);

        List<String> ids = new ArrayList<>();
        for (AudioTrack track : tracks) {
            ids.add(track.getIdentifier());
        }

        Playlist search = PlaylistManager.getPlaylistByName(owner, scope, name);
        if (search != null) {
            search.setTracks(ids);
            PlaylistManager.replacePlaylist(search);
        } else {
            PlaylistManager.savePlaylist(new Playlist(owner, name, scope, ids));
        }
    }

    public enum LoopMode {
        DISABLED,
        PLAYLIST,
        SONG
    }

}
