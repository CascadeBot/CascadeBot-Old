/*

  * Copyright (c) 2019 CascadeBot. All rights reserved.
  * Licensed under the MIT license.

 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.*;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

import static org.cascadebot.cascadebot.music.CascadePlayer.LoopMode.DISABLED;
import static org.cascadebot.cascadebot.music.CascadePlayer.LoopMode.PLAYLIST;
import static org.cascadebot.cascadebot.music.CascadePlayer.LoopMode.SONG;

public class LoopCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUIMessaging().replyUsage(this);
            return;
        }

        String loopMode = context.getMessage(0);

        if (loopMode.equals("off")) {
            context.getData().getMusicPlayer().loopMode(DISABLED);
            context.getTypedMessaging().replySuccess("Loop mode has been set to **disabled**.");
        } else if (loopMode.equals("playlist")) {
            context.getData().getMusicPlayer().loopMode(PLAYLIST);
            context.getTypedMessaging().replySuccess("Loop mode has been set to **playlist**.");
        } else if (loopMode.equals("song")) {
            context.getData().getMusicPlayer().loopMode(SONG);
            context.getTypedMessaging().replySuccess("Loop mode has been set to **song**.");
        } else {
            context.getTypedMessaging().replyDanger("I don't understand which loop mode you would like.\n To view the usage for this command, type **" + context.getData().getPrefix() + "loop**.");
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "loop";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of(
                "playlist", "Loops the current playlist", ArgumentType.COMMAND), Argument.of(
                "song", "Loops the current song", ArgumentType.COMMAND), Argument.of(
                "off", "Turns looping off", ArgumentType.COMMAND)
        );
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Loop command", "loop", true);
    }

    @Override
    public String description() {
        return "Loop command";
    }

}
