/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import io.prometheus.client.Summary;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.Environment;
import org.cascadebot.cascadebot.MDCException;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.guild.Tag;
import org.cascadebot.cascadebot.data.objects.guild.GuildData;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.metrics.Metrics;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.shared.Regex;
import org.cascadebot.shared.utils.ThreadPoolExecutorLogged;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class CommandListener extends ListenerAdapter {

    private static final ThreadGroup COMMAND_THREADS = new ThreadGroup("Command Threads");
    private static final AtomicInteger threadCounter = new AtomicInteger(0);
    private static final ExecutorService COMMAND_POOL = ThreadPoolExecutorLogged.newCachedThreadPool(r -> new Thread(COMMAND_THREADS, r, "Command Pool-" + threadCounter.incrementAndGet()), CascadeBot.LOGGER);

    private static final Pattern MULTIQUOTE_REGEX = Pattern.compile("[\"'](?=[\"'])");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getMessage().getType() != net.dv8tion.jda.api.entities.MessageType.DEFAULT || !event.getChannel().canTalk()) {
            return;
        }

        MDC.put("cascade.guild", event.getGuild().toString());
        MDC.put("cascade.sender", event.getAuthor().toString());
        MDC.put("cascade.shard_info", event.getJDA().getShardInfo().getShardString());
        MDC.put("cascade.channel", event.getChannel().toString());

        String message = Regex.MULTISPACE_REGEX.matcher(event.getMessage().getContentRaw()).replaceAll(" ");
        message = MULTIQUOTE_REGEX.matcher(message).replaceAll("");

        GuildData guildData;
        try {
            guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
            if (guildData == null) {
                // This should *hopefully* never happen but just in case :D
                throw new IllegalStateException(String.format("Guild data for guild ID: %s is null!", event.getGuild().getId()));
            }
        } catch (Exception e) {
            Messaging.sendExceptionMessage(event.getChannel(), "We have failed to process your guild data!", e);
            return;
        }

        String prefix = guildData.getCore().getPrefix();
        boolean isMention = false;

        String commandWithArgs = null;
        String trigger;
        String[] args;

        if (message.startsWith(prefix)) {
            commandWithArgs = message.substring(prefix.length()); // Remove prefix from command
        } else if (guildData.getCore().getMentionPrefix() && message.matches("^<@!?" + event.getJDA().getSelfUser().getId() + ">.*")) {
            commandWithArgs = message.substring(message.indexOf('>') + 1).trim();
            isMention = true;
        } else if (message.startsWith(Config.INS.getDefaultPrefix() + Language.i18n(guildData.getLocale(), "commands.prefix.command")) && !Config.INS.getDefaultPrefix().equals(guildData.getCore().getPrefix())) {
            commandWithArgs = message.substring(Config.INS.getDefaultPrefix().length());
        } else {
            return;
        }

        MDC.put("cascade.prefix", prefix);
        MDC.put("cascade.mention_prefix", String.valueOf(isMention));

        trigger = commandWithArgs.split(" ")[0];
        args = ArrayUtils.remove(commandWithArgs.split(" "), 0);

        MDC.put("cascade.trigger", trigger);
        MDC.put("cascade.args", Arrays.toString(args));

        try {
            processCommands(event, guildData, trigger, args, isMention);
        } catch (Exception e) {
            Messaging.sendExceptionMessage(event.getChannel(), Language.i18n(guildData.getLocale(), "responses.failed_to_process_command"), e);
            return;
        } finally {
            CascadeBot.clearCascadeMDC();
        }
    }

    private void processCommands(GuildMessageReceivedEvent event, GuildData guildData, String trigger, String[] args, boolean isMention) {
        ICommandMain cmd = CascadeBot.INS.getCommandManager().getCommand(trigger, guildData);
        CommandContext context = new CommandContext(cmd, event.getJDA(), event.getChannel(), event.getMessage(), event.getGuild(), guildData, args, event.getMember(), trigger, isMention);
        if (cmd != null) {
            if (!StringUtils.isBlank(cmd.getRequiredFlag())) {
                if (!guildData.getAllFlags().hasFlag(cmd.getRequiredFlag())) {
                    EmbedBuilder builder = MessagingObjects.getMessageTypeEmbedBuilder(org.cascadebot.cascadebot.messaging.MessageType.WARNING, event.getAuthor());
                    builder.appendDescription(Language.i18n(guildData.getLocale(), "command_meta.donate", guildData.getCore().getPrefix()));
                    event.getChannel().sendMessage(builder.build()).queue();
                    return;
                }
            }
            Metrics.INS.commandsSubmitted.labels(cmd.getClass().getSimpleName()).inc();
            if (!cmd.getModule().isPrivate() && !guildData.getCore().isModuleEnabled(cmd.getModule())) {
                if (guildData.getCore().getShowModuleErrors() || Environment.isDevelopment()) {
                    EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed(context.i18n("responses.module_for_command_disabled", FormatUtils.formatEnum(cmd.getModule(), context.getLocale()), trigger), event.getAuthor());
                    Messaging.sendEmbedMessage(MessageType.DANGER, event.getChannel(), builder, guildData.getCore().getUseEmbedForMessages());
                }
                // TODO: Modlog?
                return;
            }
            // We need to check before we process sub-commands so users can't run sub-commands with a null permission
            if (!isAuthorised(cmd, context)) {
                return;
            }
            if (args.length >= 1) {
                if (processSubCommands(cmd, args, context)) {
                    return;
                }
            }
            dispatchCommand(cmd, context);
        } else {
            if (guildData.getManagement().getAllowTagCommands()) {
                String tagName = trigger.toLowerCase();
                if (guildData.getManagement().hasTag(tagName)) {
                    Tag tag = guildData.getManagement().getTag(tagName);

                    context.reply(tag.formatTag(context)); //TODO perms for tags
                    CascadeBot.LOGGER.info("Tag {} executed by {} with args {}", tagName, context.getUser().getAsTag(), Arrays.toString(context.getArgs()));
                }
            }
        }
    }

    private boolean processSubCommands(ICommandMain cmd, String[] args, CommandContext parentCommandContext) {
        for (ICommandExecutable subCommand : cmd.getSubCommands()) {
            if (subCommand.command().equalsIgnoreCase(args[0])) {
                CommandContext subCommandContext = new CommandContext(subCommand, parentCommandContext.getJda(), parentCommandContext.getChannel(), parentCommandContext.getMessage(), parentCommandContext.getGuild(), parentCommandContext.getData(), ArrayUtils.remove(args, 0), parentCommandContext.getMember(), parentCommandContext.getTrigger() + " " + args[0], parentCommandContext.getMention());
                if (!isAuthorised(cmd, subCommandContext)) {
                    return false;
                }
                if (!StringUtils.isBlank(subCommand.getRequiredFlag())) {
                    if (!parentCommandContext.getData().getAllFlags().hasFlag(subCommand.getRequiredFlag())) {
                        EmbedBuilder builder = MessagingObjects.getMessageTypeEmbedBuilder(org.cascadebot.cascadebot.messaging.MessageType.WARNING, parentCommandContext.getUser());
                        builder.appendDescription(Language.i18n(parentCommandContext.getData().getLocale(), "command_meta.donate", parentCommandContext.getData().getCore().getPrefix()));
                        parentCommandContext.getChannel().sendMessage(builder.build()).queue();
                        return false;
                    }
                }
                return dispatchCommand(subCommand, subCommandContext);
            }
        }
        return false;
    }

    private boolean dispatchCommand(final ICommandExecutable command, final CommandContext context) {
        COMMAND_POOL.submit(() -> {
            MDC.put("cascade.sender", context.getMember().toString());
            MDC.put("cascade.guild", context.getGuild().toString());
            MDC.put("cascade.channel", context.getChannel().toString());
            MDC.put("cascade.shard_info", context.getJda().getShardInfo().getShardString());
            MDC.put("cascade.command", command.command() + (command instanceof ICommandMain ? "" : " (Sub-command)"));
            MDC.put("cascade.trigger", context.getTrigger());
            MDC.put("cascade.args", Arrays.toString(context.getArgs()));

            CascadeBot.LOGGER.info("{}Command {}{} executed by {} with args: {}", (command instanceof ICommandMain ? "" : "Sub"), command.command(), (command.command().equalsIgnoreCase(context.getTrigger()) ? "" : " (Trigger: " + context.getTrigger() + ")"), context.getUser().getAsTag(), Arrays.toString(context.getArgs()));

            Metrics.INS.commandsExecuted.labels(command.getClass().getSimpleName()).inc();
            Summary.Timer commandTimer = Metrics.INS.commandExecutionTime.labels(command.getClass().getSimpleName()).startTimer();
            try {
                command.onCommand(context.getMember(), context);
            } catch (Exception e) {
                Metrics.INS.commandsErrored.labels(command.getClass().getSimpleName()).inc();
                context.getTypedMessaging().replyException(context.i18n("responses.failed_to_run_command"), e);
                CascadeBot.LOGGER.error("Error while running a command!", MDCException.from(e));

            } finally {
                CascadeBot.clearCascadeMDC();
                commandTimer.observeDuration();
            }
        });
        deleteMessages(command, context);
        return true;
    }

    private boolean isAuthorised(ICommandExecutable command, CommandContext context) {
        if (!CascadeBot.INS.getPermissionsManager().isAuthorised(command, context.getData(), context.getMember())) {
            if (!(command instanceof ICommandRestricted)) { // Always silently fail on restricted commands, users shouldn't know what the commands are
                if (context.getCoreSettings().getShowPermErrors()) {
                    context.getUiMessaging().sendPermissionError(command.getPermission());
                }
            }
            return false;
        }
        return true;
    }

    private void deleteMessages(ICommandExecutable command, CommandContext context) {
        if (context.getCoreSettings().getDeleteCommand() && command.deleteMessages()) {
            if (context.getGuild().getSelfMember().hasPermission(context.getChannel(), Permission.MESSAGE_MANAGE)) {
                context.getMessage().delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
            } else {
                context.getGuild().getOwner().getUser().openPrivateChannel().queue(
                        channel -> channel.sendMessage(context.i18n("responses.cant_delete_guild_messages")).queue(),
                        DiscordUtils.handleExpectedErrors(ErrorResponse.CANNOT_SEND_TO_USER)
                );
            }
        }
    }

    public static void shutdownCommandPool() {
        COMMAND_POOL.shutdown();
    }


}
