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
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class PauseCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (CascadeBot.INS.getMusicHandler().getPlayer(context.getGuild().getIdLong()).getPlayer().isPaused()) {
            context.replyDanger("Music has already been paused! Use **" + context.getData().getPrefix() + "resume** to resume.");
            return;
        } else if (!CascadeBot.INS.getMusicHandler().getPlayer(context.getGuild().getIdLong()).getPlayer().isPaused()) {
            CascadeBot.INS.getMusicHandler().getPlayer(context.getGuild().getIdLong()).getPlayer().setPaused(true);
            context.replySuccess("Paused! Use **" + context.getData().getPrefix() + "resume** to resume.");
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "pause";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Pause command", "pause", true);
    }

    @Override
    public String description() {
        return "Pauses the currently playing track";
    }
}
