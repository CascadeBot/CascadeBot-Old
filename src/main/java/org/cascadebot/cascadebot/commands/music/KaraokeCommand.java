/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.music.KaraokeHandler;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import javax.xml.parsers.ParserConfigurationException;
import java.util.concurrent.ExecutionException;

public class KaraokeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getMusicPlayer().getPlayer().getPlayingTrack() == null) {
            KaraokeHandler.setKaraoke(context.getGuild().getIdLong(), false);
            context.getTypedMessaging().replyDanger(context.i18n("commands.karaoke.nothing_playing"));
            return;
        }

        if (KaraokeHandler.isKaraoke(context.getGuild().getIdLong())) {
            KaraokeHandler.setKaraoke(context.getGuild().getIdLong(), false);
            context.getTypedMessaging().replySuccess(context.i18n("commands.karaoke.disabled_karaoke"));
            return;
        } else {
            KaraokeHandler.setKaraoke(context.getGuild().getIdLong(), true);
            context.getTypedMessaging().replySuccess(context.i18n("commands.karaoke.enabled_karaoke"));
        }
        try {
            Message message = Messaging.sendInfoMessage(context.getChannel(), context.i18n("commands.karaoke.loading_karaoke")).get();
            KaraokeHandler.getSongLyrics(context.getMusicPlayer().getPlayer().getPlayingTrack().getIdentifier(), context.getChannel(), context.getGuild().getIdLong(), message);
        } catch (InterruptedException | ExecutionException e) {
            context.getTypedMessaging().replyDanger("commands.karaoke.cannot_find");
            CascadeBot.LOGGER.error("Error in karaoke command", e);
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "karaoke";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("karaoke", true);
    }
}
