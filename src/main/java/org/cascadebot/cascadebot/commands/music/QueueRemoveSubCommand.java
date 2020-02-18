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
            context.getTypedMessaging().replyDanger("`" + context.getArg(0) + "` is not a number! You can only remove using the track number.");
            return;
        }

        int index = context.getArgAsInteger(0) - 1;
        if (index < 0 || index >= context.getMusicPlayer().getQueue().size()) {
            context.getTypedMessaging().replyDanger("Cannot find track number " + (index + 1));
            return;
        }

        context.getMusicPlayer().removeTrack(index);
        context.getTypedMessaging().replySuccess("Removed track number " + (index + 1) + " from the queue.");
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
