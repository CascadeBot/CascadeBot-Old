/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.event.IPlayerEventListener;
import lavalink.client.player.event.PlayerEvent;
import lavalink.client.player.event.TrackEndEvent;
import lavalink.client.player.event.TrackExceptionEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.metrics.Metrics;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.music.TrackData;

import java.util.NoSuchElementException;

public class PlayerListener implements IPlayerEventListener, AudioEventListener {

    private CascadePlayer player;

    private int songPlayCount = 0;

    public PlayerListener(CascadePlayer player) {
        this.player = player;
    }

    @Override
    public void onEvent(PlayerEvent playerEvent) {
        if (playerEvent instanceof TrackEndEvent) {
            onEnd(((TrackEndEvent) playerEvent).getTrack());
        } else if (playerEvent instanceof TrackExceptionEvent) {
            onError(((TrackExceptionEvent) playerEvent).getException(), ((TrackExceptionEvent) playerEvent).getTrack());
        }
    }

    @Override
    public void onEvent(AudioEvent audioEvent) {
        if (audioEvent instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent) {
            onEnd(((com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent) audioEvent).track);
        } else if (audioEvent instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent) {
            onError(((com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent) audioEvent).exception, ((com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent) audioEvent).track);
        }
    }

    private void onEnd(AudioTrack track) {
        songPlayCount++;
        Metrics.INS.tracksPlayed.inc();
        try {
            if (player.getLoopMode().equals(CascadePlayer.LoopMode.DISABLED) || player.getLoopMode().equals(CascadePlayer.LoopMode.PLAYLIST)) {
                if (player.getLoopMode().equals(CascadePlayer.LoopMode.PLAYLIST)) {
                    // Add the track to the end of the queue to be repeated
                    player.getQueue().add(track.makeClone());
                    if (player.isShuffleEnabled()) {
                        if (songPlayCount % player.getQueue().size() == 0) {
                            player.shuffle(); //Shuffle when the tracks start over.
                        }
                    }
                }
                // Take the next track in the queue, remove it from the queue and play it
                AudioTrack audioTrack = player.getQueue().remove();
                player.playTrack(audioTrack);
            } else if (player.getLoopMode().equals(CascadePlayer.LoopMode.SONG)) {
                // Take the song that just finished and repeat it
                player.playTrack(track.makeClone());
            }
        } catch (NoSuchElementException e) {
            // No more songs left in the queue
            songPlayCount = 0;
            // TODO: Anything more to this?
        }
    }

    private void onError(Exception e, AudioTrack audioTrack) {
        EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        embedBuilder.setTitle(Language.i18n(((TrackData) audioTrack.getUserData()).getGuildId(), "music.misc.error"));
        embedBuilder.appendDescription(e.getCause().getCause().getMessage());
        Messaging.sendEmbedMessage(MessageType.DANGER, CascadeBot.INS.getShardManager().getTextChannelById(((TrackData) audioTrack.getUserData()).getErrorChannelId()), embedBuilder);
    }

}
