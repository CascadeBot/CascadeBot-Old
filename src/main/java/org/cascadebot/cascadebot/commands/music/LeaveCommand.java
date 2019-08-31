/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class LeaveCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        VoiceChannel voiceChannel = context.getMusicPlayer().getConnectedChannel();
        if (voiceChannel == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.voice_not_connected"));
            return;
        }

        if (!sender.getVoiceState().inVoiceChannel() || sender.getVoiceState().getChannel().equals(voiceChannel)) {
            if (!context.hasPermission("leave.other")) {
                context.getUIMessaging().sendPermissionError("leave.other");
                return;
            }
        }
        context.getMusicPlayer().leave();
        context.getTypedMessaging().replySuccess(context.i18n("commands.leave.successfully_left", voiceChannel.getName()));
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "leave";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("leave", true);
    }

}
