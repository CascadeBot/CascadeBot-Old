/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.subcommands.queue;

import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commands.music.QueueCommand;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ConfirmUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class QueueSaveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        QueueCommand.QueueResult queueResult = QueueCommand.getScopeAndOwner(this, sender, context);

        if (queueResult == null) {
            return;
        }

        long owner = queueResult.getOwner();
        PlaylistType scope = queueResult.getScope();
        CascadePlayer.SavePlaylistResult result = context.getMusicPlayer().saveCurrentPlaylist(owner, scope, context.getArg(0), false);
        switch (result) {

            case ALREADY_EXISTS:
                if (scope.equals(PlaylistType.GUILD)) {
                    if (!context.hasPermission("queue.save.overwrite")) {
                        context.getTypedMessaging().replyWarning("Playlist already exists in guild and you don't have the perm `cascade.queue.dave.overwrite` to overwrite it."); //TODO actually get the perm
                        return;
                    }
                }
                ConfirmUtils.confirmAction(sender.getUser().getIdLong(), "overwrite", context.getChannel(), MessageType.WARNING,
                        "Playlist already exists. Would you like to overwrite it?", TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(10), new ConfirmUtils.ConfirmRunnable() {
                            @Override
                            public void execute() {
                                context.getMusicPlayer().saveCurrentPlaylist(owner, scope, context.getArg(0), false);
                                context.getTypedMessaging().replySuccess("Saved playlist `" + context.getArg(0) + "`");
                            }
                        });
                break;
            case NEW:
                context.getTypedMessaging().replySuccess("Saved playlist `" + context.getArg(0) + "`");
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
