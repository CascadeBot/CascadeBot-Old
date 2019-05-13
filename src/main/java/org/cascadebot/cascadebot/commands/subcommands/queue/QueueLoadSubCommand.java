/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.subcommands.queue;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.Set;

public class QueueLoadSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this, "queue");
            return;
        }

        context.getMusicPlayer().loadPlaylist(context.getArg(0), sender, (result, tracks) -> {
            switch (result) {
                case LOADED_GUILD:
                case LOADED_USER:
                    context.getUIMessaging().sendTracksFound(tracks);
                    break;
                case EXISTS_IN_ALL_SCOPES:
                    ButtonGroup buttonGroup = new ButtonGroup(sender.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
                    buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ONE, ((runner, channel, message) -> {
                        if (!runner.equals(buttonGroup.getOwner())) {
                            return;
                        }
                        message.delete().queue();
                        context.getMusicPlayer().loadPlaylist(context.getArg(0), sender, PlaylistType.USER, ((loadPlaylistResult, newTracks) -> {
                            context.getUIMessaging().sendTracksFound(newTracks);
                        }));
                    })));
                    buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.TWO, ((runner, channel, message) -> {
                        if (!runner.equals(buttonGroup.getOwner())) {
                            return;
                        }
                        message.delete().queue();
                        context.getMusicPlayer().loadPlaylist(context.getArg(0), sender, PlaylistType.GUILD, ((loadPlaylistResult, newTracks) -> {
                            context.getUIMessaging().sendTracksFound(newTracks);
                        }));
                    })));
                    context.getUIMessaging().sendButtonedMessage("Where you like to load this track from\n" + UnicodeConstants.ONE +
                            " User\n" + UnicodeConstants.TWO + " Guild", buttonGroup);
                    break;
                case DOESNT_EXIST:
                    context.getTypedMessaging().replyDanger("Couldn't find playlist `" + context.getArg(0) + "`");
                    break;
            }
        });
    }

    @Override
    public String command() {
        return "load";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Queue load sub command", "queue.load", true);
    }

    @Deprecated(forRemoval=true)
    @Override
    public String description() {
        return null;
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("name", "Loads the playlist with the given name", ArgumentType.REQUIRED,
                Set.of(Argument.of("scope", "Loads the playlist with given name for ether you or this guild", ArgumentType.OPTIONAL))));
    }

}
