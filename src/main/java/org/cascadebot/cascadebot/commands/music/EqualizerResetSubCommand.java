/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 *  Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.HashMap;
import java.util.Map;

public class EqualizerResetSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (!CascadeBot.INS.getMusicHandler().getLavalinkEnabled()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.equalizer.not_lavalink"));
            return;
        }

        if (!(context.getMusicPlayer() instanceof CascadeLavalinkPlayer)) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.equalizer.not_lavalink"));
            return;
        }

        Map<Integer, Float> bands = new HashMap<>();
        for (int i = 0; i < Equalizer.BAND_COUNT; i++) {
            bands.put(i, 0.0f);
        }

        ((CascadeLavalinkPlayer) context.getMusicPlayer()).setBands(bands);
        context.getTypedMessaging().replySuccess(context.i18n("commands.equalizer.reset.success"));
    }

    @Override
    public String parent() {
        return "equalizer";
    }

    @Override
    public String command() {
        return "reset";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("equalizer.reset", true);
    }

    @Override
    public String getRequiredFlag() {
        return "music_controls";
    }

}
