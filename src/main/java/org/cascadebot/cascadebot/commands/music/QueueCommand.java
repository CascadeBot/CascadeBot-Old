/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.List;

public class QueueCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getMusicPlayer();
        EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();

        embedBuilder.setTitle("Queue");

        if (player.getQueue().isEmpty() && player.getPlayer().getPlayingTrack() == null) {
            context.getTypedMessaging().replyInfo("There are no tracks playing!");
            return;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("**Current song:** `").append(player.getPlayer().getPlayingTrack().getInfo().title).append("`- Request By ").append(CascadeBot.INS.getShardManager().getUserById((Long)player.getPlayer().getPlayingTrack().getUserData()).getAsTag()).append("\n\n");

        int i = 1;
        for (AudioTrack track : player.getQueue()) {
            builder.append(i).append(": `").append(track.getInfo().title).append("` - Request By ").append(CascadeBot.INS.getShardManager().getUserById((Long) track.getUserData()).getAsTag()).append("\n");
            i++;
        }

        List<Page> pages;

        if (context.getSettings().useEmbedForMessages()) {
            pages = PageUtils.splitStringToEmbedPages(builder.toString(), 1800, '\n');
        } else {
            pages = PageUtils.splitStringToStringPages(builder.toString(), 1800, '\n');
        }

        context.getUIMessaging().sendPagedMessage(pages);
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }


    @Override
    public String command() {
        return "queue";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Queue", "queue", getModule());
    }

    @Override
    public String description() {
        return "Display the current queue";
    }

}
