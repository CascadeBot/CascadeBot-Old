/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.music.TrackData;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

public class QueueLoadSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        context.getMusicPlayer().loadPlaylist(context.getArg(0), new TrackData(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong()), (result, tracks) -> {
            switch (result) {
                case LOADED_GUILD:
                case LOADED_USER:
                    context.getUiMessaging().sendTracksFound(tracks);
                    break;
                case EXISTS_IN_ALL_SCOPES:
                    ButtonGroup buttonGroup = new ButtonGroup(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
                    buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ONE, ((runner, channel, message) -> {
                        if (!runner.equals(buttonGroup.getOwner())) {
                            return;
                        }
                        message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                        context.getMusicPlayer().loadPlaylist(context.getArg(0), new TrackData(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong()), ((loadPlaylistResult, newTracks) -> {
                            context.getUiMessaging().sendTracksFound(newTracks);
                        }));
                    })));
                    buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.TWO, ((runner, channel, message) -> {
                        if (!runner.equals(buttonGroup.getOwner())) {
                            return;
                        }
                        message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                        context.getMusicPlayer().loadPlaylist(context.getArg(0), new TrackData(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong()), PlaylistType.GUILD, ((loadPlaylistResult, newTracks) -> {
                            context.getUiMessaging().sendTracksFound(newTracks);
                        }));
                    })));
                    context.getUiMessaging().sendButtonedMessage(context.i18n("commands.queue.load.load_track"), buttonGroup);
                    break;
                case DOESNT_EXIST:
                    context.getTypedMessaging().replyDanger(context.i18n("commands.queue.load.cannot_find_playlist", context.getArg(0)));
                    break;
            }
        });
    }

    @Override
    public String command() {
        return "load";
    }

    @Override
    public String parent() {
        return "queue";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("queue.load", true);
    }

    @Deprecated(forRemoval = true)
    @Override
    public String description() {
        return null;
    }

}
