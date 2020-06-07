package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.objects.GuildSettingsModeration;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModlogChannelSubCommand extends SubCommand {

    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }
        if (context.getArg(0).equalsIgnoreCase("list")) {
            StringBuilder enabled = new StringBuilder();
            StringBuilder disabled = new StringBuilder();
            StringBuilder deleted = new StringBuilder();
            var channelModlogEventsSet = context.getData().getModeration().getModlogEvents().entrySet();
            if (channelModlogEventsSet.size() == 0) {
                context.getTypedMessaging().replyDanger("No channels have been set up with modlog events!");
                return;
            }
            for (var channelModlogEventsInfo : channelModlogEventsSet) {
                TextChannel modlogChannel = CascadeBot.INS.getShardManager().getTextChannelById(channelModlogEventsInfo.getKey());
                boolean channelExists = modlogChannel != null;
                boolean someEvents = false;
                int numEvents = channelModlogEventsInfo.getValue().getEvents().size();
                if (numEvents > 5) {
                    numEvents = 3;
                    someEvents = true;
                }
                boolean webhookExists = isEnabled(modlogChannel, context);
                String eventsListString = channelModlogEventsInfo.getValue().getEvents().stream().limit(numEvents).map(event -> Language.i18n(context.getLocale(), "enums.modlogevent." + event.name().toLowerCase() + ".display")).collect(Collectors.joining(", "));
                if (someEvents) {
                    eventsListString += ", and " + (channelModlogEventsInfo.getValue().getEvents().size() - 3) + " more";
                }
                if (channelExists && webhookExists) { // Channel exists and is enabled
                    enabled.append(modlogChannel.getAsMention()).append(" - ").append(eventsListString).append('\n');
                } else if (channelExists) { // Channel exists, but is disabled
                    disabled.append(modlogChannel.getAsMention()).append(" - ").append(eventsListString).append('\n');
                } else { // Channel doesn't exist
                    deleted.append("#deleted-channel (").append(channelModlogEventsInfo.getValue().getId()).append(") - ").append(eventsListString).append('\n');
                }
            }
            List<Page> pages = new ArrayList<>();
            if (enabled.length() > 0) {
                EmbedBuilder enabledBuilder = new EmbedBuilder();
                enabledBuilder.setTitle("Channel list - Enabled");
                enabledBuilder.setDescription(enabled.toString());
                pages.add(new PageObjects.EmbedPage(enabledBuilder));
            }
            if (disabled.length() > 0) {
                EmbedBuilder disabledBuilder = new EmbedBuilder();
                disabledBuilder.setTitle("Channel list - disabled");
                disabledBuilder.setDescription(disabled.toString());
                pages.add(new PageObjects.EmbedPage(disabledBuilder));
            }
            if (deleted.length() > 0) {
                EmbedBuilder deletedBuilder = new EmbedBuilder();
                deletedBuilder.setTitle("Channel list - deleted channels");
                deletedBuilder.setDescription(deleted.toString());
                pages.add(new PageObjects.EmbedPage(deletedBuilder));
            }
            context.getUiMessaging().sendPagedMessage(pages);
            return;
        }
        if (context.getArgs().length == 3 && context.getArg(0).equalsIgnoreCase("move")) {
            TextChannel targetChannel = DiscordUtils.getTextChannel(context.getGuild(), context.getArg(2));
            if (targetChannel == null) {
                context.getTypedMessaging().replyDanger("Couldn't find channel `" + context.getArg(2) + "`");
                return;
            }
            GuildSettingsModeration.ChannelModlogEventsInfo channelModlogEventsInfo = null;
            if (context.isArgInteger(1)) {
                int id = context.getArgAsInteger(1);
                for (var eventsInfoEntry : context.getData().getModeration().getModlogEvents().entrySet()) {
                    if (eventsInfoEntry.getValue().getId() == id) {
                        channelModlogEventsInfo = eventsInfoEntry.getValue();
                    }
                }
            } else {
                TextChannel originalChannel = DiscordUtils.getTextChannel(context.getGuild(), context.getArg(1));
                if (originalChannel != null) {
                    channelModlogEventsInfo = context.getData().getModeration().getModlogEvents().get(originalChannel.getIdLong());
                }
            }
            if (channelModlogEventsInfo == null) {
                context.reply("Couldn't find events for channel `" + context.getArg(1) + "`");
                return;
            }
            for (ModlogEvent event : channelModlogEventsInfo.getEvents()) {
                context.getData().getModeration().enableEvent(targetChannel, event);
            }
            context.getTypedMessaging().replySuccess("Successfully moved all events!");
            return;
        }
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }
        TextChannel channel = DiscordUtils.getTextChannel(context.getGuild(), context.getArg(1));
        if (channel == null) {
            context.getTypedMessaging().replyDanger("Couldn't find channel `" + context.getArg(1) + "`");
            return;
        }
        if (!context.getData().getModeration().getModlogEvents().containsKey(channel.getIdLong())) {
            context.getTypedMessaging().replyDanger("The specified channel is not a modlog channel!");
            return;
        }
        if (context.getArg(0).equalsIgnoreCase("enable")) {
            if (isEnabled(channel, context)) {
                context.getTypedMessaging().replyDanger("Channel is already enabled!");
                return;
            }
            channel.createWebhook("cascade-modlog").queue(webhook -> {
                context.getData().getModeration().getModlogEvents().get(channel.getIdLong()).setNewWebhook(webhook.getIdLong(), webhook.getToken());
                context.getTypedMessaging().replySuccess("Successfully disabled channel!");
            }, throwable -> {
                context.getTypedMessaging().replyDanger("Failed to enable events! The bot probably doesn't have permissions to create webhooks.");
            });
        } else if (context.getArg(0).equalsIgnoreCase("disable")) {
            if (!isEnabled(channel, context)) {
                context.getTypedMessaging().replyDanger("Channel is already disabled!");
                return;
            }
            channel.deleteWebhookById(String.valueOf(context.getData().getModeration().getModlogEvents().get(channel.getIdLong()).getWebhookId())).queue(aVoid -> {
                context.getTypedMessaging().replySuccess("Successfully enabled channel!");
            }, throwable -> {
                context.getTypedMessaging().replyDanger("Failed to disable events! The bot probably doesn't have permissions to delete webhooks.");
            });
        } else if (context.getArg(0).equalsIgnoreCase("delete")) {
            context.getData().getModeration().getModlogEvents().remove(channel.getIdLong());
            context.getTypedMessaging().replySuccess("Successfully deleted channel!");
        } else if (context.getArg(0).equalsIgnoreCase("info")) {
            GuildSettingsModeration.ChannelModlogEventsInfo channelModlogEventsInfo = context.getData().getModeration().getModlogEvents().get(channel.getIdLong());
            String events = channelModlogEventsInfo.getEvents().stream().map(event -> Language.i18n(context.getLocale(), "enums.modlogevent." + event.name().toLowerCase() + ".display")).collect(Collectors.joining("\n"));
            context.getUiMessaging().sendPagedMessage(PageUtils.splitStringToEmbedPages(events, "Events for channel " + channel.getName(), 1000, '\n'));
        } else {
            context.getUiMessaging().replyUsage();
        }
    }

    private boolean isEnabled(TextChannel channel, CommandContext context) {
        boolean webhookExists = false;
        if (channel != null) {
            List<Webhook> webhooks = channel.retrieveWebhooks().complete();
            for (Webhook webhook : webhooks) {
                if (webhook.getIdLong() == context.getData().getModeration().getModlogEvents().get(channel.getIdLong()).getWebhookId()) {
                    webhookExists = true;
                    break;
                }
            }
        }
        return webhookExists;
    }

    public String command() {
        return "channel";
    }

    public CascadePermission permission() {
        return CascadePermission.of("modlog.channel", false);
    }

    public String parent() {
        return "modlog";
    }

}
