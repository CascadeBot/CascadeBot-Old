/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.MusicHandler;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.EventWaiter;
import org.cascadebot.cascadebot.utils.StringsUtil;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.concurrent.TimeUnit;

public class SearchCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        CascadeBot.INS.getMusicHandler().searchTracks(context.getMessage(0), context.getChannel(), searchResults -> {
            ButtonGroup buttonGroup = new ButtonGroup(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
            int i = 0;
            StringBuilder messageBuilder = new StringBuilder();
            for (MusicHandler.SearchResult result : searchResults) {
                i++;
                char unicode = (char) (0x0030 + i); //This is setting up the first unicode character to be 003n where n is equal to i.
                buttonGroup.addButton(new Button.UnicodeButton(unicode + "\u20E3", (runner, channel, message) -> {
                    if (!runner.equals(buttonGroup.getOwner())) {
                        return;
                    }
                    message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                    context.getMusicPlayer().loadLink(result.getUrl(), sender.getIdLong(), nothing -> {
                        context.getTypedMessaging().replyWarning(context.i18n("commands.search.cannot_find_video"));
                    }, exception -> {
                        context.getTypedMessaging().replyException(context.i18n("commands.search.error_loading_track"), exception);
                    }, audioTracks -> {
                        context.getMusicPlayer().addTracks(audioTracks);
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
            embedBuilder.setTitle(context.i18n("commands.search.multiple_results"));
            embedBuilder.setDescription(messageBuilder.toString());

            try {
                context.getUIMessaging().sendButtonedMessage(embedBuilder.build(), buttonGroup);
            } catch (PermissionException e) {
                embedBuilder.appendDescription("\n\n" + context.i18n("responses.type_one_of"));
                for (int index = 1; index <= searchResults.size(); index++) {
                    embedBuilder.appendDescription("`").appendDescription(String.valueOf(index)).appendDescription("` ");
                }

                context.getTypedMessaging().replyInfo(embedBuilder);

                EventWaiter.TextResponse[] responses = new EventWaiter.TextResponse[searchResults.size()];
                for (int index = 0; index < searchResults.size(); index++) {
                    MusicHandler.SearchResult result = searchResults.get(index);
                    responses[index] = new EventWaiter.TextResponse(event -> {
                        context.getMusicPlayer().loadLink(result.getUrl(), sender.getIdLong(), nothing -> {
                            context.getTypedMessaging().replyWarning(context.i18n("commands.search.cannot_find_video"));
                        }, exception -> {
                            context.getTypedMessaging().replyException(context.i18n("commands.search.error_loading_track"), exception);
                        }, audioTracks -> {
                            context.getMusicPlayer().addTracks(audioTracks);
                            context.getUIMessaging().sendTracksFound(audioTracks);
                        });
                    }, String.valueOf(index + 1));
                }
                CascadeBot.INS.getEventWaiter().waitForResponse(context.getUser(), context.getChannel(), 30, TimeUnit.SECONDS, () -> {
                    context.getTypedMessaging().replyWarning(context.i18n("commands.search.search_timed_out", context.getMessage(0 )));
                }, responses);
            }

        });
    }

    @Override
    public String command() {
        return "search";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("search", false);
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

}
