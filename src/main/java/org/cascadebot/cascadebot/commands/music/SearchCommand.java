/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.MusicHandler;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.StringsUtil;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

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
                char unicode = (char) (0x0030 + i); //This is setting up the first unicode character to be 003n where n is equal to i.
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
                messageBuilder.append(unicode).append("\u20E3").append(" - ").append(StringsUtil.truncate(result.getTitle(), 60)).append(" - ");
                switch (result.getType()) {
                    case VIDEO:
                        messageBuilder.append(UnicodeConstants.SONG);
                        break;
                    case PLAYLIST:
                        messageBuilder.append(UnicodeConstants.PLAYLIST);
                        break;
                }
                messageBuilder.append('\n');
            }

            EmbedBuilder embedBuilder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO);
            embedBuilder.setTitle("We found multiple results for this search!");
            embedBuilder.setDescription(messageBuilder.toString());

            context.getUIMessaging().sendButtonedMessage(embedBuilder.build(), buttonGroup);

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
