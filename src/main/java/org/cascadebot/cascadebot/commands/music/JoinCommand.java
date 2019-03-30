/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.shared.Regex;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class JoinCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        VoiceChannel voiceChannel = sender.getVoiceState().getChannel();

        if (context.getArgs().length == 1) {
            Matcher matcher = Regex.ID.matcher(context.getArg(0));
            if (matcher.matches()) {
                voiceChannel = context.getGuild().getVoiceChannelById(matcher.group(0));
            } else {
                List<VoiceChannel> voiceChannels = FinderUtil.findVoiceChannels(context.getArg(0), context.getGuild());
                voiceChannel = voiceChannels.isEmpty() ? null : voiceChannels.get(0);
            }
            if (voiceChannel == null) {
                context.getTypedMessaging().replyDanger("Could not find that voice channel to connect to!");
                return;
            }
        } else if (voiceChannel == null) {
            context.getTypedMessaging().replyDanger("You are not connected to a voice channel!");
            return;
        }

        if (!context.getSelfMember().hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
            context.getUIMessaging().sendBotPermissionError(Permission.VOICE_CONNECT);
            return;
        } else if (!context.getSelfMember().hasPermission(voiceChannel, Permission.VOICE_SPEAK)) {
            context.getUIMessaging().sendBotPermissionError(Permission.VOICE_SPEAK);
            return;
        }

        if (context.getData().getMusicPlayer().getConnectedChannel() != null) {
            if (context.getData().getMusicPlayer().getConnectedChannel().equals(voiceChannel)) {
                context.getTypedMessaging().replyWarning("I am already connected to your channel!");
                return;
            } else {
                if (!context.hasPermission("join.other")) {
                    context.getUIMessaging().sendPermissionError("join.other");
                    return;
                }
            }
        }
        context.getData().getMusicPlayer().join(voiceChannel);
        context.getTypedMessaging().replySuccess("I have successfully joined the voice channel `#%s`", voiceChannel.getName());
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("summon", "connect", "getinhere");
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
        return "Joins the bot to a voice channel";
    }

}
