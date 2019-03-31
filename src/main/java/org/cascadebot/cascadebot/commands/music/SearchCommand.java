/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.music.MusicHandler;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.buttons.IButtonRunnable;

import java.util.Set;

public class SearchCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if(context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this);
            return;
        }

        CascadeBot.INS.getMusicHandler().searchTracks(context.getMessage(0), context.getChannel(), searchResults -> {
            ButtonGroup buttonGroup = new ButtonGroup(sender.getUser().getIdLong(), context.getGuild().getIdLong());
            int i = 0;
            StringBuilder messageBuilder = new StringBuilder();
            for(MusicHandler.SearchResult result : searchResults) {
                i++;
                char unicode = (char) (0x0030 + i);
                buttonGroup.addButton(new Button.UnicodeButton(unicode + "\u20E3", (runner, channel, message) -> {
                    if (!runner.equals(buttonGroup.getOwner())) {
                        return;
                    }
                    message.delete().queue();
                    context.getData().getMusicPlayer().loadLink(result.getUrl(), nothing -> {
                        context.getTypedMessaging().replyWarning("Couldn't find video!");
                    }, exception -> {
                        context.getTypedMessaging().replyException("Error loading track", exception);
                    }, audioTracks -> {
                        context.getUIMessaging().sendTracksFound(audioTracks);
                    });
                }));
                messageBuilder.append(result.getTitle()).append(" - ");
                switch (result.getType()) {

                    case VIDEO:
                        messageBuilder.append("\uD83C\uDFB5");
                        break;
                    case PLAYLIST:
                        messageBuilder.append("\uD83C\uDFB6");
                        break;
                }
                messageBuilder.append('\n');
            }

            context.getUIMessaging().sendButtonedMessage(messageBuilder.toString(), buttonGroup);

        });
    }

    @Override
    public String command() {
        return "search";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Search", "search", getModule());
    }

    @Override
    public String description() {
        return "Searches for a song";
    }

    @Override
    public Set<Argument> getArguments() {
        return Set.of(Argument.of("search", "search for a specific song", ArgumentType.REQUIRED));
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("find");
    }
}
