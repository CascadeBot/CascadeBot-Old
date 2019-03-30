/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class StopCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getData().getMusicPlayer().getPlayer().getPlayingTrack() != null) { // If the playing track isn't null, and the bot isn't paused
            context.getData().getMusicPlayer().stop();
            context.replySuccess("Music has been stopped!");
        } else {
            context.replyDanger("Music isn't playing, nothing to stop!");
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
        return CascadePermission.of("Stop music command", "stop", true);
    }

    @Override
    public String description() {
        return "Stops music when playing";
    }

}
