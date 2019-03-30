/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class JoinCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        VoiceChannel voiceChannel = sender.getVoiceState().getChannel();
        if (voiceChannel == null) {
            context.replyDanger("You are not connected to a voice channel!");
            return;
        }

        if (context.getData().getMusicPlayer().getConnectedChannel() != null) {
            if (context.getData().getMusicPlayer().getConnectedChannel().equals(voiceChannel)) {
                context.replyWarning("I am already connected to your channel!");
                return;
            } else {
                if (context.hasPermission("join.other")) {
                    context.getData().getMusicPlayer().join(voiceChannel);
                } else {
                    context.replyDanger("");
                }
            }
            
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("summon");
    }

    @Override
    public String command() {
        return "join";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Join command", "join", true);
    }

    @Override
    public String description() {
        return null;
    }

}
