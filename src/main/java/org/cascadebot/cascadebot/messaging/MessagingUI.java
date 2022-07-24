/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ExecutableCommand;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer;
import org.cascadebot.cascadebot.utils.pagination.Page;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class MessagingUI {

    private static final Pattern YOUTUBE_VIDEO_REGEX = Pattern.compile("v=(?<v>[a-zA-Z0-9_-]{11})");

    private CommandContext context;

    public MessagingUI(CommandContext context) {
        this.context = context;
    }

    /**
     * Sends an image to the channel in the context
     * When embeds are on, it embeds the image into the embed.
     * When embeds are off, it downloads the image and sends it via the {@link net.dv8tion.jda.api.entities.TextChannel#sendFile(File, AttachmentOption...)} method
     *
     * @param url The url of the image.
     * @return A {@link CompletableFuture<Message>} so you can interact with the message after it sends.
     */
    public CompletableFuture<Message> replyImage(String url) {
        if (true) {
            EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder(context.getUser(), context.getLocale());
            embedBuilder.setImage(url);
            return context.getChannel().sendMessage(embedBuilder.build()).submit();
        } else {
            String[] split = url.split("/");
            try {
                return context.getChannel().sendFile(new URL(url).openStream(), split[split.length - 1]).submit();
            } catch (IOException e) {
                return Messaging.sendExceptionMessage(context.getChannel(), context.i18n("responses.error_loading_image"), e);
            }
        }
    }

    /**
     * Sends a message with components.
     *
     * @param message   The String message to send.
     * @param container The {@link ComponentContainer} to use for components.
     * @see ComponentContainer
     */
    public void sendComponentMessage(String message, ComponentContainer container) {
        Messaging.sendComponentMessage(context.getChannel(), message, container);
    }

    /**
     * Sends a message with components.
     *
     * @param embed     The {@link MessageEmbed} to use as the message.
     * @param container The {@link ComponentContainer} to use for components.
     * @see ComponentContainer
     */
    public void sendComponentMessage(MessageEmbed embed, ComponentContainer container) {
        Messaging.sendComponentMessage(context.getChannel(), embed, container);
    }

    /**
     * Sends a message with reactions to function as "buttons".
     *
     * @param message   The message to send.
     * @param container The {@link ComponentContainer} to use for components.
     * @see ComponentContainer
     */
    public void sendComponentMessage(Message message, ComponentContainer container) {
        Messaging.sendComponentMessage(context.getChannel(), message, container);
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
//        CascadePermission permission = CascadeBot.INS.getPermissionsManager().getPermission(stringPermission);
//        Checks.notNull(permission, "permission");
//        sendPermissionError(permission);
        // TODO: Slash command fixes
    }

    /**
     * Sends a permission error.
     *
     * @param permission The Discord Permission that the user doesn't have.
     */
    public void sendUserDiscordPermError(Permission permission) {
        context.getTypedMessaging().replyDanger(context.i18n("responses.no_discord_perm", permission.getName()));
    }

    public void sendBotDiscordPermError(Permission permission) {
        context.getTypedMessaging().replyDanger(context.i18n("responses.no_discord_perm_bot", permission.getName()));
    }

    public void replyUsage() {
        replyUsage(context.getCommand());
    }

    public void replyUsage(ExecutableCommand command) {
        /*String usage = context.getUsage(command);
        List<Page> pages = PageUtils.splitStringToEmbedPages(usage, context.i18n("commands.usage.title", command.fullCommand(context.getLocale())), 1000, '\n');
        pages.addAll(command.additionalUsagePages(context.getLocale()));
        sendPagedMessage(pages);*/
    }

    public void sendTracksFound(List<AudioTrack> tracks) {
        if (tracks.size() > 1) {
            long time = 0;
            for (AudioTrack track : tracks) {
                time += track.getDuration();
            }
            context.getTypedMessaging().replySuccess(context.i18n("music.misc.loaded_tracks", tracks.size(), FormatUtils.formatLongTimeMills(time)));
        } else {
            AudioTrack track = tracks.get(0);
            EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder(context.getUser(), context.getLocale());
            builder.setTitle(context.i18n("music.misc.loaded_track"));
            builder.setDescription(track.getInfo().title);
            if (!track.getInfo().isStream) {
                builder.addField(context.i18n("words.length"), FormatUtils.formatLongTimeMills(track.getDuration()), true);
            }
            builder.addField(context.i18n("words.author"), track.getInfo().author, true);
            context.getTypedMessaging().replySuccess(builder);
        }
    }

}
