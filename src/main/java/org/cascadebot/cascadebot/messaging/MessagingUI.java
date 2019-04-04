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
import net.dv8tion.jda.core.requests.RequestFuture;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.CommandException;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.pagination.Page;
import spark.utils.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class MessagingUI {

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
        if (context.getSettings().useEmbedForMessages()) {
            EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
            embedBuilder.setImage(url);
            return context.getChannel().sendMessage(embedBuilder.build()).submit();
        } else {
            String[] split = url.split("/");
            try {
                return context.getChannel().sendFile(new URL(url).openStream(), split[split.length - 1]).submit();
            } catch (IOException e) {
                return Messaging.sendExceptionMessage(context.getChannel(), "Error loading image", new CommandException(e, context.getGuild(), context.getTrigger()));
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
        CascadePermission permission = CascadeBot.INS.getPermissionsManager().getPermission(stringPermission);
        if (!CollectionUtils.isEmpty(permission.getDiscordPerm())) {
            EnumSet<Permission> permissions = permission.getDiscordPerm();
            String discordPerms = permissions.stream()
                    .map(Permission::getName)
                    .map(p -> "`" + p + "`")
                    .collect(Collectors.joining(", "));
            context.getTypedMessaging().replyDanger("You don't have the permission `%s`, or the Discord permission(s) %s to do this!", permission.getPermissionNode(), discordPerms);
        } else {
            context.getTypedMessaging().replyDanger("You don't have the permission `%s` to do this!", permission.getPermissionNode());
        }
    }

    /**
     * Sends a permission error.
     *
     * @param permission The Discord Permission that the user doesn't have.
     */
    public void sendUserPermissionError(Permission permission) {
        context.getTypedMessaging().replyDanger("You don't have the Discord permission `%s` to do this!", permission.getName());
    }

    public void sendBotPermissionError(Permission permission) {
        context.getTypedMessaging().replyDanger("I don't have the Discord permission `%s` to do this!", permission.getName());
    }

    public void replyUsage(ICommandExecutable command) {
        replyUsage(command, null);
    }

    public void replyUsage(ICommandExecutable command, String parent) {
        context.getTypedMessaging().replyWarning("Incorrect usage. Proper usage:\n" + context.getUsage(command, parent));
    }

    public void sendTracksFound(List<AudioTrack> tracks) {
        if(tracks.size() > 1) {
            long time = 0;
            for(AudioTrack track : tracks) {
                time += track.getDuration();
            }
            context.getTypedMessaging().replySuccess("Loaded `%s` tracks with a total length of `%s`", tracks.size(), FormatUtils.formatLongTimeMills(time));
        } else {
            AudioTrack track = tracks.get(0);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Loaded Track");
            builder.setDescription(track.getInfo().title);
            builder.addField("Length", FormatUtils.formatLongTimeMills(track.getDuration()), true);
            builder.addField("Author", track.getInfo().author, true);
            context.getTypedMessaging().replySuccess(builder);
        }
    }

}
