/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.music;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandMain;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.permissions.CascadePermission;

import java.util.Set;

public class LeaveCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        VoiceChannel voiceChannel = context.getMusicPlayer().getConnectedChannel();
        if (voiceChannel == null) {
            context.getTypedMessaging().replyDanger("I am not connected to a voice channel!");
            return;
        }

        if (!sender.getVoiceState().inVoiceChannel() || sender.getVoiceState().getChannel().equals(voiceChannel)) {
            if (!context.hasPermission("leave.other")) {
                context.getUIMessaging().sendPermissionError("leave.other");
                return;
            }
        }
        context.getMusicPlayer().leave();
        context.getTypedMessaging().replySuccess("I have successfully left the channel `#%s`", voiceChannel.getName());
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("gtfo");
    }

    @Override
    public String command() {
        return "leave";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Leave command", "leave", true);
    }

    @Override
    public String description() {
        return "Tells the bot to leave a voice channel";
    }

}
