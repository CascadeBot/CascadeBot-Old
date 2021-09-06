/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.music.TrackData;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow;
import org.cascadebot.cascadebot.utils.interactions.CascadeButton;
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer;

public class QueueLoadSubCommand extends DeprecatedSubCommand {

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
                    ComponentContainer container = new ComponentContainer();
                    CascadeActionRow actionRow = new CascadeActionRow();
                    actionRow.addComponent(CascadeButton.primary(Emoji.fromUnicode(UnicodeConstants.ONE), ((runner, channel, message) -> {
                        if (!runner.equals(context.getMember())) {
                            return;
                        }
                        message.getMessage().delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                        context.getMusicPlayer().loadPlaylist(context.getArg(0), new TrackData(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong()), ((loadPlaylistResult, newTracks) -> {
                            context.getUiMessaging().sendTracksFound(newTracks);
                        }));
                    })));
                    actionRow.addComponent(CascadeButton.primary(Emoji.fromUnicode(UnicodeConstants.TWO), ((runner, channel, message) -> {
                        if (!runner.equals(context.getMember())) {
                            return;
                        }
                        message.getMessage().delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                        context.getMusicPlayer().loadPlaylist(context.getArg(0), new TrackData(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong()), PlaylistType.GUILD, ((loadPlaylistResult, newTracks) -> {
                            context.getUiMessaging().sendTracksFound(newTracks);
                        }));
                    })));
                    container.addRow(actionRow);
                    context.getUiMessaging().sendComponentMessage(context.i18n("commands.queue.load.load_track"), container);
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
