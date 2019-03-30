/*
 *
 *  * Copyright (c) 2019 CascadeBot. All rights reserved.
 *  * Licensed under the MIT license.
 *
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class ResumeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getData().getMusicPlayer().getPlayer().isPaused()) {
            context.getData().getMusicPlayer().getPlayer().setPaused(false);
            context.getTypedMessaging().replySuccess("Music has been resumed!");
        } else {
            context.getTypedMessaging().replyDanger("Music is already playing! Use **" + context.getData().getPrefix() + "pause** to pause it.");
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
