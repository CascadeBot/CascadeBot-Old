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

import static org.cascadebot.cascadebot.music.CascadePlayer.LoopMode.*;

public class LoopCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getData().getMusicPlayer().getLoopMode() == DISABLED) {
            context.getData().getMusicPlayer().loopMode(PLAYLIST);
            context.getTypedMessaging().replySuccess("Loop mode has been set to `playlist`.");
            return;
        } if (context.getData().getMusicPlayer().getLoopMode() == PLAYLIST) {
            context.getData().getMusicPlayer().loopMode(SONG);
            context.getTypedMessaging().replySuccess("Loop mode has been set to `song`.");
            return;
        } if (context.getData().getMusicPlayer().getLoopMode() == SONG) {
            context.getData().getMusicPlayer().loopMode(DISABLED);
            context.getTypedMessaging().replySuccess("Loop mode has been set to `disabled`.");
        } else {
            context.getTypedMessaging().replyDanger("I can't change the loop mode.");
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
    public CascadePermission getPermission() {
        return CascadePermission.of("Loop command", "loop", true);
    }

    @Override
    public String description() {
        return "Changes the loop mode";
    }

}
