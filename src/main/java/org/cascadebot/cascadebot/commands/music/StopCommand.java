/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class StopCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getMusicPlayer().getPlayingTrack() != null) { // If the playing track isn't null, and the bot isn't paused
            context.getMusicPlayer().stop();
            context.getTypedMessaging().replySuccess(context.i18n("commands.stop.stopped"));
        } else {
            context.getTypedMessaging().replyDanger(context.i18n("commands.stop.not_stopped"));
        }
    }

    @Override
    public Module module() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "stop";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("stop", false);
    }

}
