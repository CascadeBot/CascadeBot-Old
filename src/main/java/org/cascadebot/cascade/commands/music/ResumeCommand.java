/*
 *
 *  * Copyright (c) 2019 CascadeBot. All rights reserved.
 *  * Licensed under the MIT license.
 *
 */

package org.cascadebot.cascade.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandMain;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.permissions.CascadePermission;

public class ResumeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getMusicPlayer().getPlayer().isPaused()) {
            context.getMusicPlayer().getPlayer().setPaused(false);
            context.getTypedMessaging().replySuccess("Music has been resumed!");
        } else {
            context.getTypedMessaging().replyDanger("Music is already playing! Use **" + context.getSettings().getPrefix() + "pause** to pause it.");
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "resume";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Resume command", "resume", true);
    }

    @Override
    public String description() {
        return "Resumes music";
    }

}
