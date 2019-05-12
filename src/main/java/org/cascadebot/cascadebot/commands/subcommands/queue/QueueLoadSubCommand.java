/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.subcommands.queue;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commands.music.QueueCommand;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class QueueLoadSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        QueueCommand.QueueResult queueResult = QueueCommand.getScopeAndOwner(this, sender, context);

        if (queueResult == null) {
            return;
        }

        PlaylistType scope = queueResult.getScope();

        context.getMusicPlayer().loadPlaylist(context.getArg(0), sender, scope, (result, tracks) -> {
            switch (result) {

                case LOADED_GUILD:
                case LOADED_USER:
                    context.getUIMessaging().sendTracksFound(tracks);
                    break;
                case EXISTS_IN_ALL_SCOPES:
                    //TODO edit confirm utils to allow multiple options
                    break;
                case DOESNT_EXISTS:
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
