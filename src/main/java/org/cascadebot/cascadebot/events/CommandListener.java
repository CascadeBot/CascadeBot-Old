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
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.Environment;
import org.cascadebot.cascadebot.MDCException;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ExecutableCommand;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.RestrictedCommand;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.entities.GuildFilterEntity;
import org.cascadebot.cascadebot.data.entities.GuildModuleEntity;
import org.cascadebot.cascadebot.data.entities.GuildSettingsCoreEntity;
import org.cascadebot.cascadebot.data.entities.GuildSettingsManagementEntity;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.CommandFilter;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.metrics.Metrics;
import org.cascadebot.cascadebot.utils.DatabaseUtilsKt;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.shared.Regex;
import org.cascadebot.shared.utils.ThreadPoolExecutorLogged;
import org.hibernate.Session;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class CommandListener extends ListenerAdapter {

    private static final ThreadGroup COMMAND_THREADS = new ThreadGroup("Command Threads");
    private static final AtomicInteger threadCounter = new AtomicInteger(0);
    private static final ExecutorService COMMAND_POOL = ThreadPoolExecutorLogged.newCachedThreadPool(r -> new Thread(COMMAND_THREADS, r, "Command Pool-" + threadCounter.incrementAndGet()), CascadeBot.LOGGER);

    private static final ThreadLocal<Session> sqlSession = new ThreadLocal<>();

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

        String prefix = ";";
        boolean isMention = false;

        String commandWithArgs = null;
        String trigger;
        String[] args;

        if (message.startsWith(prefix)) {
            commandWithArgs = message.substring(prefix.length()); // Remove prefix from command
        } else if (message.startsWith(Config.INS.getDefaultPrefix() + Language.i18n(Locale.ENGLISH_UK /* TODO local */, "commands.prefix.command")) && !Config.INS.getDefaultPrefix().equals(prefix)) {
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
            processCommands(event, trigger, args, isMention);
        } catch (Exception e) {
            Messaging.sendExceptionMessage(event.getChannel(), Language.i18n(Locale.ENGLISH_UK /* TODO local */, "responses.failed_to_process_command"), e);
            return;
        } finally {
            CascadeBot.clearCascadeMDC();
        }
    }

    private void processCommands(GuildMessageReceivedEvent event, String trigger, String[] args, boolean isMention) {
        MainCommand cmd = CascadeBot.INS.getCommandManager().getCommand(trigger, event.getGuild().getIdLong());
        CommandContext context = new CommandContext(cmd, event.getJDA(), event.getChannel(), event.getMessage(), event.getGuild(), args, event.getMember(), trigger, isMention);
        GuildSettingsCoreEntity coreSettings = context.getDataObject(GuildSettingsCoreEntity.class);
        if (coreSettings == null) {
            throw new UnsupportedOperationException("This shouldn't happen");
        }
        GuildModuleEntity guildModuleEntity = context.getDataObject(GuildModuleEntity.class);
        if (guildModuleEntity == null) {
            throw new UnsupportedOperationException("This shouldn't happen");
        }
        if (cmd != null) {
            Metrics.INS.commandsSubmitted.labels(cmd.getClass().getSimpleName()).inc();
            if (!cmd.module().isPrivate() && !guildModuleEntity.getModuleEnabled(cmd.module())) {
                if (coreSettings.getModuleErrors() || Environment.isDevelopment()) {
                    EmbedBuilder builder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.DANGER, event.getAuthor(), context.getLocale())
                            .setDescription(context.i18n("responses.module_for_command_disabled", FormatUtils.formatEnum(cmd.module(), context.getLocale()), trigger));
                    context.getTimedMessaging().sendAutoDeleteMessage(builder.build(), 5000);
                }
                // TODO: Modlog?
                return;
            }
            // We need to check before we process sub-commands so users can't run sub-commands with a null permission
            if (!isAuthorised(cmd, context)) {
                return;
            }

            if (!processFilters(cmd, context)) {
                GuildSettingsManagementEntity managementSettings = context.getDataObject(GuildSettingsManagementEntity.class);
                if (managementSettings == null) {
                    throw new UnsupportedOperationException("This shouldn't happen");
                }
                // TODO: Moderation log event
                if (managementSettings.getDisplayFilterError()) {
                    var message = context.i18n("commands.filters.message_blocked") +
                            (context.getMember().hasPermission(Permission.ADMINISTRATOR) ? context.i18n("commands.filters.message_blocked_admin") : "");
                    context.getTypedMessaging().replyDanger(message);
                }
                return;
            }

            if (args.length >= 1) {
                if (processSubCommands(cmd, args, context)) {
                    return;
                }
            }
            dispatchCommand(cmd, context);
        }
    }

    private boolean processFilters(MainCommand cmd, CommandContext context) {
        List<GuildFilterEntity> filters = context.transaction(session -> {
            return DatabaseUtilsKt.listOf(session, GuildFilterEntity.class, "guild_id", context.getGuildId());
        });
        if (filters == null) {
            return false;
        }
        for (GuildFilterEntity guildFilterEntity : filters) {
            /*if (filter.evaluateFilter(cmd.command(), context.getChannel(), context.getMember()) == CommandFilter.FilterResult.DENY
                    && !context.hasPermission("filters.bypass"))  {
                return false;
            }*/
        }
        return true;
    }

    private boolean processSubCommands(MainCommand cmd, String[] args, CommandContext parentCommandContext) {
        for (ExecutableCommand subCommand : cmd.subCommands()) {
            if (subCommand.command(parentCommandContext.getLocale()).equalsIgnoreCase(args[0])) {
                CommandContext subCommandContext = new CommandContext(subCommand, parentCommandContext.getJda(), parentCommandContext.getChannel(), parentCommandContext.getMessage(), parentCommandContext.getGuild(), ArrayUtils.remove(args, 0), parentCommandContext.getMember(), parentCommandContext.getTrigger() + " " + args[0], parentCommandContext.getMention());
                if (!isAuthorised(cmd, subCommandContext)) {
                    return false;
                }
                return dispatchCommand(subCommand, subCommandContext);
            }
        }
        return false;
    }

    private boolean dispatchCommand(final ExecutableCommand command, final CommandContext context) {
        COMMAND_POOL.submit(() -> {
            Session commandSession = CascadeBot.INS.getPostgresManager().getSessionFactory().openSession();
            sqlSession.set(commandSession);
            MDC.put("cascade.sender", context.getMember().toString());
            MDC.put("cascade.guild", context.getGuild().toString());
            MDC.put("cascade.channel", context.getChannel().toString());
            MDC.put("cascade.shard_info", context.getJda().getShardInfo().getShardString());
            MDC.put("cascade.command", command.command() + (command instanceof MainCommand ? "" : " (Sub-command)"));
            MDC.put("cascade.trigger", context.getTrigger());
            MDC.put("cascade.args", Arrays.toString(context.getArgs()));

            CascadeBot.LOGGER.info("{}Command {}{} executed by {} with args: {}", (command instanceof MainCommand ? "" : "Sub"), command.command(), (command.command().equalsIgnoreCase(context.getTrigger()) ? "" : " (Trigger: " + context.getTrigger() + ")"), context.getUser().getAsTag(), Arrays.toString(context.getArgs()));

            Metrics.INS.commandsExecuted.labels(command.getClass().getSimpleName()).inc();
            Summary.Timer commandTimer = Metrics.INS.commandExecutionTime.labels(command.getClass().getSimpleName()).startTimer();
            try (commandSession) {
                command.onCommand(context.getMember(), context);
            } catch (Exception e) {
                Metrics.INS.commandsErrored.labels(command.getClass().getSimpleName()).inc();
                context.getTypedMessaging().replyException(context.i18n("responses.failed_to_run_command"), e);
                CascadeBot.LOGGER.error("Error while running a command!", MDCException.from(e));

            } finally {
                CascadeBot.clearCascadeMDC();
                commandTimer.observeDuration();
                sqlSession.remove();
            }
        });
        deleteMessages(command, context);
        return true;
    }

    private boolean isAuthorised(ExecutableCommand command, CommandContext context) {
        GuildSettingsCoreEntity coreSettings = context.getDataObject(GuildSettingsCoreEntity.class);
        if (coreSettings == null) {
            throw new UnsupportedOperationException("This shouldn't happen");
        }
        if (!CascadeBot.INS.getPermissionsManager().isAuthorised(command, context.getMember())) {
            if (!(command instanceof RestrictedCommand)) { // Always silently fail on restricted commands, users shouldn't know what the commands are
                if (coreSettings.getPermErrors()) {
                    context.getUiMessaging().sendPermissionError(command.permission());
                }
            }
            return false;
        }
        return true;
    }

    private void deleteMessages(ExecutableCommand command, CommandContext context) {
        GuildSettingsCoreEntity coreSettings = context.getDataObject(GuildSettingsCoreEntity.class);
        if (coreSettings == null) {
            throw new UnsupportedOperationException("This shouldn't happen");
        }
        if (coreSettings.getDeleteCommand() && command.deleteMessages()) {
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

    public static ThreadLocal<Session> getSqlSession() {
        return sqlSession;
    }

}
