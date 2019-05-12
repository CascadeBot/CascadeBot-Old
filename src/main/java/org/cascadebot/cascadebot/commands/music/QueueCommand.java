/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commands.subcommands.queue.QueueLoadSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.queue.QueueSaveSubCommand;
import org.cascadebot.cascadebot.data.objects.PlaylistType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

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

        //builder.append("**Current song:** `").append(player.getPlayer().getPlayingTrack().getInfo().title).append("`- Request By ").append(CascadeBot.INS.getShardManager().getUserById((Long)player.getPlayer().getPlayingTrack().getUserData()).getAsTag()).append("\n\n\n");

        int i = 1;
        for (AudioTrack track : player.getQueue()) {
            builder.append(i).append(". **").append(track.getInfo().title).append("**\n Request by ").append(CascadeBot.INS.getShardManager().getUserById((Long) track.getUserData()).getAsTag()).append("\n\n");
            if (i % 10 == 0) {
                pages.add(new PageObjects.EmbedPage(new EmbedBuilder().setDescription(builder.toString())));
                builder = new StringBuilder();
            }
            i++;
        }

        pages.add(new PageObjects.EmbedPage(new EmbedBuilder().setDescription(builder.toString())));

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

    public static QueueResult getScopeAndOwner(ICommandExecutable commandExecutable, Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(commandExecutable, "queue");
            return null;
        }

        PlaylistType scope = PlaylistType.GUILD;
        if (context.getArgs().length > 1) {
            scope = EnumUtils.getEnum(PlaylistType.class, context.getArg(1).toUpperCase());
            if (scope == null) {
                context.getTypedMessaging().replyDanger("Scope `" + context.getArg(1) + "` not found");
                return null;
            }
        }

        long owner = 0;
        switch (scope) {

            case GUILD:
                owner = context.getGuild().getIdLong();
                break;
            case USER:
                owner = sender.getUser().getIdLong();
                break;
        }

        return new QueueResult(scope, owner);
    }

    public static class QueueResult {

        PlaylistType scope;
        long owner;

        public QueueResult(PlaylistType scope, long owner) {
            this.scope = scope;
            this.owner = owner;
        }

        public PlaylistType getScope() {
            return scope;
        }

        public long getOwner() {
            return owner;
        }
    }

}
