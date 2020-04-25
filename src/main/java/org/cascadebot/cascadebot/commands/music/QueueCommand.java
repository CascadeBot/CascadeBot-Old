/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.music.TrackData;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;
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

        embedBuilder.setTitle(context.i18n("words.queue"));

        if (player.getQueue().isEmpty()) {
            context.getTypedMessaging().replyInfo(context.i18n("commands.queue.no_tracks_playing"));
            return;
        }

        StringBuilder builder = new StringBuilder();

        List<Page> pages = new ArrayList<>();

        int i = 1;
        for (AudioTrack track : player.getQueue()) {
            builder.append(i).append(". **").append(track.getInfo().title).append("**");
            if (track.getUserData() instanceof TrackData) {
                long id = ((TrackData) track.getUserData()).getUserId();
                User user = CascadeBot.INS.getShardManager().getUserById(id);
                if (user == null) {
                    continue;
                }
                builder.append("\n").append(context.i18n("words.requested_by")).append(user.getAsTag());
            }
            builder.append("\n\n");
            if (i % 10 == 0) {
                pages.add(new PageObjects.EmbedPage(new EmbedBuilder().setDescription(builder.toString() + context.i18n("commands.queue.queue_time", FormatUtils.formatLongTimeMills((long) player.getQueueLength())))));
                builder = new StringBuilder();
            }
            i++;
        }

        if (builder.toString().length() > 0) {
            pages.add(new PageObjects.EmbedPage(new EmbedBuilder().setDescription(builder.toString() + context.i18n("commands.queue.queue_time", FormatUtils.formatLongTimeMills((long) player.getQueueLength())))));
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
        return CascadePermission.of("queue", false);
    }

    @Override
    public Set<ISubCommand> getSubCommands() {
        return Set.of(new QueueSaveSubCommand(), new QueueLoadSubCommand(), new QueueMoveSubCommand(), new QueueRemoveSubCommand());
    }

}
