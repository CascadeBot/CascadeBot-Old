/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
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
                context.getTypedMessaging().replyDanger(context.i18n("responses.cannot_find_voice_channel"));
                return;
            }
        } else if (voiceChannel == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.join.user_not_connected"));
            return;
        }

        if (!context.getSelfMember().hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
            context.getUIMessaging().sendBotDiscordPermError(Permission.VOICE_CONNECT);
            return;
        } else if (!context.getSelfMember().hasPermission(voiceChannel, Permission.VOICE_SPEAK)) {
            context.getUIMessaging().sendBotDiscordPermError(Permission.VOICE_SPEAK);
            return;
        }

        if (context.getMusicPlayer().getConnectedChannel() != null) {
            if (context.getMusicPlayer().getConnectedChannel().equals(voiceChannel)) {
                context.getTypedMessaging().replyWarning(context.i18n("commands.join.already_connected"));
                return;
            } else {
                if (!context.hasPermission("join.other")) {
                    context.getUIMessaging().sendPermissionError("join.other");
                    return;
                }
            }
        }
        context.getMusicPlayer().join(voiceChannel);
        context.getTypedMessaging().replySuccess(context.i18n("commands.join.successfully_joined", voiceChannel.getName()));
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "join";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("join", true);
    }

}
