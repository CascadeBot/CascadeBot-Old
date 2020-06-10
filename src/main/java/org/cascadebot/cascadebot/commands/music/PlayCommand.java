/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.music.TrackData;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class PlayCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getData().getMusic().getJoinOnPlay()) {
            VoiceChannel voiceChannel = sender.getVoiceState().getChannel();
            if (context.getMusicPlayer().getConnectedChannel() != null) {
                if (context.getMusicPlayer().getConnectedChannel() != voiceChannel) {
                    context.runOtherCommand("join", sender, context);
                }
            }
        }

        if (context.getArgs().length == 0) {
            context.runOtherCommand("resume", sender, context);
        } else if (context.getArgs().length == 1 && context.getArg(0).startsWith("http")) {
            context.getMusicPlayer().loadLink(context.getArg(0), new TrackData(context.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong()), input -> {
                context.getTypedMessaging().replyDanger(context.i18n("commands.play.could_not_find_matches", input));
            }, exception -> {
                context.getTypedMessaging().replyException(context.i18n("commands.play.encountered_error"), exception);
            }, tracks -> {
                context.getUiMessaging().checkPlaylistOrSong(context.getArg(0), tracks, context, false);
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
                        context.getMusicPlayer().addTracks(tracks);
                        context.getUiMessaging().sendTracksFound(tracks);
                    });
                }
            });
        }
    }

    @Override
    public Module module() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "play";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("play", true);
    }

}
