/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascade.commandmeta.Argument;
import org.cascadebot.cascade.commandmeta.ArgumentType;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandExecutable;
import org.cascadebot.cascade.data.objects.PlaylistType;
import org.cascadebot.cascade.messaging.MessageType;
import org.cascadebot.cascade.music.CascadePlayer;
import org.cascadebot.cascade.permissions.CascadePermission;
import org.cascadebot.cascade.utils.ConfirmUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class QueueSaveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this, "queue");
            return;
        }

        PlaylistType scope = PlaylistType.GUILD;
        if (context.getArgs().length > 1) {
            scope = EnumUtils.getEnum(PlaylistType.class, context.getArg(1).toUpperCase());
            if (scope == null) {
                context.getTypedMessaging().replyDanger("Scope `" + context.getArg(1) + "` not found");
                return;
            }
        }

        long owner = 0;
        switch (scope) {
            case GUILD:
                owner = context.getGuild().getIdLong();
                break;
            case USER:
                owner = sender.getUser().getIdLong();
                break;
        }

        long lambdaOwner = owner;
        PlaylistType lambdaScope = scope;
        CascadePlayer.SavePlaylistResult result = context.getMusicPlayer().saveCurrentPlaylist(lambdaOwner, lambdaScope, context.getArg(0), false);
        switch (result) {
            case ALREADY_EXISTS:
                if (lambdaScope.equals(PlaylistType.GUILD)) {
                    if (!context.hasPermission("queue.save.overwrite")) {
                        context.getTypedMessaging().replyWarning("Playlist already exists in guild and you don't have the perm `cascade.queue.dave.overwrite` to overwrite it."); //TODO actually get the perm
                        return;
                    }
                }
                ConfirmUtils.confirmAction(sender.getUser().getIdLong(), "overwrite", context.getChannel(), MessageType.WARNING,
                        "Playlist already exists. Would you like to overwrite it?", TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(10), new ConfirmUtils.ConfirmRunnable() {
                            @Override
                            public void execute() {
                                context.getMusicPlayer().saveCurrentPlaylist(lambdaOwner, lambdaScope, context.getArg(0), false);
                                context.getTypedMessaging().replySuccess("Saved playlist `" + context.getArg(0) + "` in scope `" + lambdaScope.name().toLowerCase() + "`");
                            }
                        });
                break;
            case NEW:
                context.getTypedMessaging().replySuccess("Saved playlist `" + context.getArg(0) + "` in scope `" + lambdaScope.name().toLowerCase() + "`");
                break;
        }
    }

    @Override
    public String command() {
        return "save";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Queue save sub command", "queue.save", true);
    }

    @Deprecated(forRemoval = true)
    @Override
    public String description() {
        return null;
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("name", "Saves the current queue with the given name", ArgumentType.REQUIRED,
                Set.of(Argument.of("scope", "Saves the current queue with given name for ether you or this guild", ArgumentType.OPTIONAL))));
    }

}
