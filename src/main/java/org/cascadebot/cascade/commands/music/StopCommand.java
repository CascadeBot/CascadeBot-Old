/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandMain;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.permissions.CascadePermission;

public class StopCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getMusicPlayer().getPlayer().getPlayingTrack() != null) { // If the playing track isn't null, and the bot isn't paused
            context.getMusicPlayer().stop();
            context.getTypedMessaging().replySuccess("Music has been stopped!");
        } else {
            context.getTypedMessaging().replyDanger("Music isn't playing, nothing to stop!");
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "stop";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Stop music command", "stop", false);
    }

    @Override
    public String description() {
        return "Stops music when playing";
    }

}
