/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.Cascade;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class PlayCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.runOtherCommand("resume", sender, context);
        } else if (context.getArgs().length == 1 && context.getArg(0).startsWith("http")) {
            context.getMusicPlayer().loadLink(context.getArg(0), sender.getUser().getIdLong(), input -> {
                context.getTypedMessaging().replyDanger("We could not find music that matches: `%s`", input);
            }, exception -> {
                context.getTypedMessaging().replyException("We encountered an error processing that!", exception);
            }, tracks -> {
                context.getUIMessaging().checkPlaylistOrSong(context.getArg(0), tracks, context);
            });
        } else {
            Cascade.INS.getMusicHandler().searchTracks(context.getMessage(0), context.getChannel(), searchResults -> {
                if (searchResults.isEmpty()) {
                    context.getTypedMessaging().replyDanger("We could not find music that matches: `%s`", context.getArg(0));
                } else {
                    context.getMusicPlayer().loadLink(searchResults.get(0).getUrl(), sender.getUser().getIdLong(), itShouldMatch -> {}, exception -> {
                        context.getTypedMessaging().replyException("We encountered an error processing that!", exception);
                    }, tracks -> {
                        context.getMusicPlayer().addTracks(tracks);
                        context.getUIMessaging().sendTracksFound(tracks);
                    });
                }
            });
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "play";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Play command", "play", true);
    }

    @Override
    public String description() {
        return "Plays music";
    }

}
