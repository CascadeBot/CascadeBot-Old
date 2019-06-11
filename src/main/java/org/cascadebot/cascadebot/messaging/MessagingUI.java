/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.RequestFuture;
import net.dv8tion.jda.core.utils.Checks;
import org.cascadebot.cascadebot.Cascade;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.EventWaiter;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.pagination.Page;
import spark.utils.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessagingUI {

    private static final Pattern YOUTUBE_VIDEO_REGEX = Pattern.compile("v=(?<v>[a-zA-Z0-9_-]{11})");

    private CommandContext context;

    public MessagingUI(CommandContext context) {
        this.context = context;
    }

    /**
     * Sends an image to the channel in the context
     * When embeds are on, it embeds the image into the embed.
     * When embeds are off, it downloads the image and sends it via the {@link net.dv8tion.jda.core.entities.TextChannel#sendFile(File)} method
     *
     * @param url The url of the image.
     * @return A {@link RequestFuture<Message>} so you can interact with the message after it sends.
     */
    public RequestFuture<Message> replyImage(String url) {
        if (context.getSettings().isUseEmbedForMessages()) {
            EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
            embedBuilder.setImage(url);
            return context.getChannel().sendMessage(embedBuilder.build()).submit();
        } else {
            String[] split = url.split("/");
            try {
                return context.getChannel().sendFile(new URL(url).openStream(), split[split.length - 1]).submit();
            } catch (IOException e) {
                return Messaging.sendExceptionMessage(context.getChannel(), "Error loading image", e);
            }
        }
    }

    /**
     * Sends a message with reactions to function as "buttons".
     *
     * @param message The String message to send.
     * @param group   The {@link ButtonGroup} to use for buttons.
     * @see ButtonGroup
     */
    public void sendButtonedMessage(String message, ButtonGroup group) {
        Messaging.sendButtonedMessage(context.getChannel(), message, group);
    }

    /**
     * Sends a message with reactions to function as "buttons".
     *
     * @param embed The {@link MessageEmbed} to use as the message.
     * @param group The {@link ButtonGroup} to use for buttons.
     * @see ButtonGroup
     */
    public void sendButtonedMessage(MessageEmbed embed, ButtonGroup group) {
        Messaging.sendButtonedMessage(context.getChannel(), embed, group);
    }

    /**
     * Sends a message with reactions to function as "buttons".
     *
     * @param message The message to send.
     * @param group   The {@link ButtonGroup} to use for buttons.
     * @see ButtonGroup
     */
    public void sendButtonedMessage(Message message, ButtonGroup group) {
        Messaging.sendButtonedMessage(context.getChannel(), message, group);
    }

    /**
     * Sends a pages message with buttons for page navigation.
     *
     * @param pages The list of pages to use. see {@link org.cascadebot.cascadebot.utils.pagination.PageObjects.EmbedPage}, {@link org.cascadebot.cascadebot.utils.pagination.PageObjects.StringPage}, and {@link org.cascadebot.cascadebot.utils.pagination.PageObjects.TablePage}.
     * @see org.cascadebot.cascadebot.utils.pagination.PageObjects.EmbedPage
     * @see org.cascadebot.cascadebot.utils.pagination.PageObjects.StringPage
     * @see org.cascadebot.cascadebot.utils.pagination.PageObjects.TablePage
     */
    public void sendPagedMessage(List<Page> pages) {
        Messaging.sendPagedMessage(context.getChannel(), context.getMember(), pages);
    }

    /**
     * Sends a permission error.
     *
     * @param stringPermission The Cascade permission that the user doesn't have.
     */
    public void sendPermissionError(String stringPermission) {
        CascadePermission permission = Cascade.INS.getPermissionsManager().getPermission(stringPermission);
        Checks.notNull(permission, "permission");
        sendPermissionError(permission);
    }

    public void sendPermissionError(CascadePermission permission) {
        Checks.notNull(permission, "permission");
        if (!CollectionUtils.isEmpty(permission.getDiscordPerms())) {
            EnumSet<Permission> permissions = permission.getDiscordPerms();
            String discordPerms = permissions.stream()
                    .map(Permission::getName)
                    .map(p -> "`" + p + "`")
                    .collect(Collectors.joining(", "));
            context.getTypedMessaging().replyDanger("You don't have the permission `%s` or the Discord permission(s) %s that you need to do this!", permission.getPermission(), discordPerms);
        } else {
            context.getTypedMessaging().replyDanger("You don't have the permission `%s` that you need to do this!", permission.getPermission());
        }
    }

    /**
     * Sends a permission error.
     *
     * @param permission The Discord Permission that the user doesn't have.
     */
    public void sendUserDiscordPermError(Permission permission) {
        context.getTypedMessaging().replyDanger("You don't have the Discord permission `%s` that you need to do this!", permission.getName());
    }

    public void sendBotDiscordPermError(Permission permission) {
        context.getTypedMessaging().replyDanger("I don't have the Discord permission `%s` that I need to do this!", permission.getName());
    }

    public void replyUsage(ICommandExecutable command) {
        replyUsage(command, null);
    }

    public void replyUsage(ICommandExecutable command, String parent) {
        context.getTypedMessaging().replyWarning("Incorrect usage. Proper usage:\n" + context.getUsage(command, parent));
    }

    public void sendTracksFound(List<AudioTrack> tracks) {
        if (tracks.size() > 1) {
            long time = 0;
            for (AudioTrack track : tracks) {
                time += track.getDuration();
            }
            context.getTypedMessaging().replySuccess("Loaded `%s` tracks with a total length of `%s`", tracks.size(), FormatUtils.formatLongTimeMills(time));
        } else {
            AudioTrack track = tracks.get(0);
            EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder(context.getUser());
            builder.setTitle("Loaded Track");
            builder.setDescription(track.getInfo().title);
            if (!track.getInfo().isStream) {
                builder.addField("Length", FormatUtils.formatLongTimeMills(track.getDuration()), true);
            }
            builder.addField("Author", track.getInfo().author, true);
            context.getTypedMessaging().replySuccess(builder);
        }
    }

    public void checkPlaylistOrSong(String input, List<AudioTrack> tracks, CommandContext context) {
        if (tracks.size() > 1) {

            Matcher matcher = YOUTUBE_VIDEO_REGEX.matcher(input);
            if (!matcher.find() || matcher.group("v") == null) {
                context.getMusicPlayer().addTracks(tracks);
                context.getUIMessaging().sendTracksFound(tracks);
                return;
            }

            AudioTrack selectedTrack = tracks.stream().filter(audioTrack -> audioTrack.getIdentifier().equals(matcher.group("v"))).findFirst().orElse(tracks.get(0));

            ButtonGroup buttonGroup = new ButtonGroup(context.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
            buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.SONG, (runner, channel, message) -> {
                message.delete().queue();
                context.getMusicPlayer().addTrack(selectedTrack);
                context.getUIMessaging().sendTracksFound(Collections.singletonList(selectedTrack));
            }));
            buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.PLAYLIST, (runner, channel, message) -> {
                message.delete().queue();
                context.getMusicPlayer().addTracks(tracks);
                context.getUIMessaging().sendTracksFound(tracks);
            }));

            String message = String.format(UnicodeConstants.SONG + " - Load as track `%s`\n" +
                            UnicodeConstants.PLAYLIST + " - Load as playlist `%s`",
                    selectedTrack.getInfo().title, tracks.size() + " tracks");

            EmbedBuilder embedBuilder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, context.getUser());
            embedBuilder.setTitle("Load as a single track or as a playlist?");
            embedBuilder.setDescription(message);

            try {
                context.getUIMessaging().sendButtonedMessage(embedBuilder.build(), buttonGroup);
            } catch (PermissionException e) {
                context.getTypedMessaging().replyInfo(embedBuilder.appendDescription("\n\n" + "Please type either `track` or `playlist`!"));

                Cascade.INS.getEventWaiter().waitForResponse(context.getUser(), context.getChannel(),
                        new EventWaiter.TextResponse(event -> {
                            context.getMusicPlayer().addTrack(selectedTrack);
                            context.getUIMessaging().sendTracksFound(Collections.singletonList(selectedTrack));
                        }, "track"),
                        new EventWaiter.TextResponse(event -> {
                            context.getMusicPlayer().addTracks(tracks);
                            context.getUIMessaging().sendTracksFound(tracks);
                        }, "playlist"));
            }

        } else if (tracks.size() == 1) {
            context.getMusicPlayer().addTracks(tracks);
            context.getUIMessaging().sendTracksFound(tracks);
        } else {
            context.getTypedMessaging().replyDanger("We couldn't find any tracks to load!");
        }
    }

}
