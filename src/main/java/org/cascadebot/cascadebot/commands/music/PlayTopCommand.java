/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.music.TrackData;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayTopCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUiMessaging().replyUsage();
        } else if (context.getArgs().length == 1 && context.getArg(0).startsWith("http")) {
            context.getMusicPlayer().loadLink(context.getArg(0), new TrackData(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong()), input -> {
                context.getTypedMessaging().replyDanger(context.i18n("commands.play.could_not_find_matches", input));
            }, exception -> {
                context.getTypedMessaging().replyException(context.i18n("commands.play.encountered_error"), exception);
            }, tracks -> {
                context.getUiMessaging().checkPlaylistOrSong(context.getArg(0), tracks, context, true);
            });
        } else {
            CascadeBot.INS.getMusicHandler().searchTracks(context.getMessage(0), context.getChannel(), searchResults -> {
                if (searchResults.isEmpty()) {
                    context.getTypedMessaging().replyDanger(context.i18n("commands.play.could_not_find_matches", context.getArg(0)));
                } else {
                    context.getMusicPlayer().loadLink(searchResults.get(0).getUrl(), new TrackData(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong()), itShouldMatch -> {
                    }, exception -> {
                        context.getTypedMessaging().replyException(context.i18n("commands.play.encountered_error"), exception);
                    }, tracks -> {
                        List<AudioTrack> currentQueue = new ArrayList<>(context.getMusicPlayer().getQueue());
                        AudioTrack topTrack = tracks.remove(0);
                        currentQueue.addAll(0, tracks);
                        context.getMusicPlayer().setQueue(new LinkedList<>(currentQueue));
                        context.getMusicPlayer().playTrack(topTrack);
                        context.getUiMessaging().sendTracksFound(tracks);
                    });
                }
            });
        }
    }

    @Override
    public String command() {
        return "playtop";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("playtop", false);
    }

    @Override
    public Module module() {
        return Module.MUSIC;
    }

}
