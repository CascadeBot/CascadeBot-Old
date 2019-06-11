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

public class ShuffleCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getMusicPlayer().toggleShuffleOnRepeat()) {
            context.getMusicPlayer().shuffle();
            context.getTypedMessaging().replySuccess("Shuffling has been enabled!");
        } else {
            context.getTypedMessaging().replySuccess("Shuffling has been disabled!");
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "shuffle";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Shuffle command", "shuffle", true);
    }

    @Override
    public String description() {
        return "Shuffles music in a queue";
    }

}
