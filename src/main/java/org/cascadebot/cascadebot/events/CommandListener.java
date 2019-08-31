/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import io.prometheus.client.Summary;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.apache.commons.lang3.ArrayUtils;
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
import org.cascadebot.cascadebot.data.objects.guild.GuildData;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.metrics.Metrics;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.shared.Regex;
import org.cascadebot.shared.utils.ThreadPoolExecutorLogged;
import org.slf4j.MDC;

import java.util.ArrayList;
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
        if (event.getAuthor().isBot() || event.getMessage().getType() != MessageType.DEFAULT || !event.getChannel().canTalk()) {
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

        String prefix = guildData.getCoreSettings().getPrefix();
        boolean isMention = false;

        String commandWithArgs;
        String trigger;
        String[] args;

        if (message.startsWith(prefix)) {
            commandWithArgs = message.substring(prefix.length()); // Remove prefix from command
        } else if (guildData.getCoreSettings().isMentionPrefix() && message.startsWith(event.getJDA().getSelfUser().getAsMention())) {
            commandWithArgs = message.substring(event.getJDA().getSelfUser().getAsMention().length()).trim();
            isMention = true;
        } else if (message.startsWith(Config.INS.getDefaultPrefix() + Language.i18n(guildData.getLocale(), "commands.prefix.command")) && !Config.INS.getDefaultPrefix().equals(guildData.getCoreSettings().getPrefix())) {
            commandWithArgs = message.substring(Config.INS.getDefaultPrefix().length());
        } else {
            return;
        }

        MDC.put("cascade.prefix", prefix);
        MDC.put("cascade.mention_prefix", String.valueOf(isMention));

        trigger = commandWithArgs.split(" ")[0];
        commandWithArgs = commandWithArgs.substring(trigger.length()).trim();
        args = splitArgs(commandWithArgs);

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

    public String[] splitArgs(String input) {
        final char NONE = 0;        
        char quoteType = NONE; // Whether the current position is surrounded by quotes or not and what type
        char bracketType = NONE;
        int splitFrom = NONE; // We initially start the first split from NONE to the first space
        var args = new ArrayList<String>();
        for (int pos = NONE; pos < input.length(); pos++) {
            char charAtPos = input.charAt(pos);
            if (charAtPos == ' ') {
                // If we are in a quote or bracket scope then we want to ignore this space
                if (quoteType != NONE || bracketType != NONE) {
                    continue;
                }
                int splitTo = pos;

                if (input.charAt(pos - 1) == '"' || input.charAt(pos - 1) == '\'') {
                    splitTo = pos - 1; // If we are splitting after a quote, don't include the quote in the split
                }
                args.add(input.substring(splitFrom, splitTo));
                splitFrom = pos + 1; // Set the next split start to be after
            } else if (pos == input.length() - 1) {
                int splitTo = input.length();
                // If the end character is a quote, we want to split before the quote to not include it.
                if (quoteType != NONE && input.charAt(pos) == quoteType) {
                    splitTo = pos;
                }
                args.add(input.substring(splitFrom, splitTo));
                // End of string so do nothing else
            } else if (charAtPos == '"' || charAtPos == '\'') {
                if (bracketType != NONE) continue;
                // If we are not already in quotes AND [the quote is at the start OR it has a space before it] AND there is a quote to close the scope
                if (quoteType == NONE && (pos == 0 || input.charAt(pos - 1) == ' ') && input.substring(pos + 1).indexOf(charAtPos) != -1) {
                    splitFrom += 1; // Start the split after the first quote
                    quoteType = charAtPos;
                } else if (quoteType == charAtPos) {
                    quoteType = NONE;
                }
            } else if (charAtPos == '(' || charAtPos == '{' || charAtPos == '[') {
                // If we are not in a bracket scope AND there is a closing bracket
                if (bracketType == NONE && input.substring(pos + 1).indexOf(getClosingBracket(charAtPos)) != -1) {
                    bracketType = charAtPos;
                }
            } else if (charAtPos == ')' || charAtPos == '}' || charAtPos == ']') {
                if (getClosingBracket(bracketType) == charAtPos) {
                    bracketType = NONE;
                }
            }

        }
        return args.toArray(String[]::new);
    }

    private static char getClosingBracket(char opening) {
        if (opening == '(') {
            return ')';
        } else if (opening == '{') {
            return '}';
        } else if (opening == '[') {
            return ']';
        } else {
            return '\u0000';
        }
    }

    private void processCommands(GuildMessageReceivedEvent event, GuildData guildData, String trigger, String[] args, boolean isMention) {
        ICommandMain cmd = CascadeBot.INS.getCommandManager().getCommand(trigger, guildData);
        CommandContext context = new CommandContext(cmd, event.getJDA(), event.getChannel(), event.getMessage(), event.getGuild(), guildData, args, event.getMember(), trigger, isMention);
        if (cmd != null) {
            Metrics.INS.commandsSubmitted.labels(cmd.getClass().getSimpleName()).inc();
            if (!cmd.getModule().isPrivate() && !guildData.getCoreSettings().isModuleEnabled(cmd.getModule())) {
                if (guildData.getCoreSettings().isShowModuleErrors() || Environment.isDevelopment()) {
                    EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed(context.i18n("responses.module_for_command_disabled", FormatUtils.formatEnum(cmd.getModule(), context.getLocale()), trigger), event.getAuthor());
                    Messaging.sendDangerMessage(event.getChannel(), builder, guildData.getCoreSettings().isUseEmbedForMessages());
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
            if (guildData.getCoreSettings().isAllowTagCommands()) {
                if (guildData.getCoreSettings().getTags().containsKey(trigger)) {
                    Tag tag = guildData.getCoreSettings().getTag(trigger);

                    context.reply(tag.formatTag(context)); //TODO perms for tags
                    CascadeBot.LOGGER.info("Tag {} executed by {} with args {}", trigger, context.getUser().getAsTag(), Arrays.toString(context.getArgs()));
                }
            }
        }
    }

    private boolean processSubCommands(ICommandMain cmd, String[] args, CommandContext parentCommandContext) {
        for (ICommandExecutable subCommand : cmd.getSubCommands()) {
            if (subCommand.command().equalsIgnoreCase(args[0])) {
                CommandContext subCommandContext = new CommandContext(subCommand, parentCommandContext.getJda(), parentCommandContext.getChannel(), parentCommandContext.getMessage(), parentCommandContext.getGuild(), parentCommandContext.getData(), ArrayUtils.remove(args, 0), parentCommandContext.getMember(), parentCommandContext.getTrigger() + " " + args[0], parentCommandContext.isMention());
                if (!isAuthorised(cmd, subCommandContext)) {
                    return false;
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
                if (context.getCoreSettings().isShowPermErrors()) {
                    context.getUIMessaging().sendPermissionError(command.getPermission());
                }
            }
            return false;
        }
        return true;
    }

    private void deleteMessages(ICommandExecutable command, CommandContext context) {
        if (context.getCoreSettings().isDeleteCommand() && command.deleteMessages()) {
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
