/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.Cascade;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        List<Page> pages = new ArrayList<>();

        int i = 1;
        for (AudioTrack track : player.getQueue()) {
            builder.append(i).append(". **").append(track.getInfo().title).append("**");
            if(track.getUserData() instanceof Long) {
                builder.append("\n Requested by ").append(Cascade.INS.getShardManager().getUserById((Long) track.getUserData()).getAsTag());
            }
            builder.append("\n\n");
            if (i % 10 == 0) {
                pages.add(new PageObjects.EmbedPage(new EmbedBuilder().setDescription(builder.toString())));
                builder = new StringBuilder();
            }
            i++;
        }

        if (builder.toString().length() > 0) {
            pages.add(new PageObjects.EmbedPage(new EmbedBuilder().setDescription(builder.toString())));
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
    public Set<String> getGlobalAliases() {
        return Set.of("playlist");
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Queue", "queue", getModule());
    }

    @Override
    public Set<ICommandExecutable> getSubCommands() {
        return Set.of(new QueueSaveSubCommand(), new QueueLoadSubCommand());
    }

    @Override
    public String description() {
        return "Display the current queue";
    }

}
