/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ConfirmUtils;

import java.util.concurrent.TimeUnit;

public class QueueSaveSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        PlaylistType scope = PlaylistType.GUILD;
        if (context.getArgs().length > 1) {
            scope = EnumUtils.getEnum(PlaylistType.class, context.getArg(1).toUpperCase());
            if (scope == null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.queue.save.scope_not_found", context.getArg(1)));
                return;
            }
        }

        if (context.getMusicPlayer().getPlayer().getPlayingTrack() == null & context.getMusicPlayer().getQueue().size() == 0) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.queue.save.nothing_to_save"));
            return;
        }

        long owner = 0;
        switch (scope) {
            case GUILD:
                owner = context.getGuild().getIdLong();
                break;
            case USER:
                owner = sender.getIdLong();
                break;
        }

        long lambdaOwner = owner;
        PlaylistType lambdaScope = scope;
        CascadePlayer.SavePlaylistResult result = context.getMusicPlayer().saveCurrentPlaylist(lambdaOwner, lambdaScope, context.getArg(0), false);
        switch (result) {
            case ALREADY_EXISTS:
                if (lambdaScope.equals(PlaylistType.GUILD)) {
                    if (!context.hasPermission("queue.save.overwrite")) {
                        context.getTypedMessaging().replyWarning(context.i18n("commands.queue.save.saved_playlist"));
                        return;
                    }
                }
                ConfirmUtils.confirmAction(sender.getIdLong(), "overwrite", context.getChannel(), MessageType.WARNING,
                        context.i18n("commands.save.already_exists"), TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(10), new ConfirmUtils.ConfirmRunnable() {
                            @Override
                            public void execute() {
                                context.getMusicPlayer().saveCurrentPlaylist(lambdaOwner, lambdaScope, context.getArg(0), false);
                                context.getTypedMessaging().replySuccess(context.i18n("commands.queue.save.saved_playlist", context.getArg(0), lambdaScope.name().toLowerCase()));
                            }
                        });
                break;
            case NEW:
                context.getTypedMessaging().replySuccess(context.i18n("commands.queue.save.saved_playlist", context.getArg(0), lambdaScope.name().toLowerCase()));
                break;
        }
    }

    @Override
    public String command() {
        return "save";
    }

    @Override
    public String parent() {
        return "queue";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("queue.save", true);
    }

    @Deprecated(forRemoval = true)
    @Override
    public String description() {
        return null;
    }

}
