/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class QueueRemoveSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUIMessaging().replyUsage(this);
            return;
        }

        if (!context.isArgInteger(0)) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.queue.remove.invalid_number", context.getArg(0)));
            return;
        }

        int index = context.getArgAsInteger(0) - 1;
        if (index < 0 || index >= context.getMusicPlayer().getQueue().size()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.queue.track_number", index + 1));
            return;
        }

        context.getMusicPlayer().removeTrack(index);
        context.getTypedMessaging().replySuccess(context.i18n("commands.queue.remove.removed", index + 1));
    }

    @Override
    public String command() {
        return "remove";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("queue.remove", false);
    }

    @Override
    public String parent() {
        return "queue";
    }

}
