/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.ArrayUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.Environment;
import org.cascadebot.cascadebot.MDCException;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import org.cascadebot.cascadebot.commandmeta.ModuleFlag;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.shared.Regex;
import org.cascadebot.shared.utils.ThreadPoolExecutorLogged;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class CommandListener extends ListenerAdapter {

    private static final ThreadGroup COMMAND_THREADS = new ThreadGroup("Command Threads");
    private static final AtomicInteger threadCounter = new AtomicInteger(0);
    private static final ExecutorService COMMAND_POOL = ThreadPoolExecutorLogged.newFixedThreadPool(5, r ->
            new Thread(COMMAND_THREADS, r, "Command Pool-" + threadCounter.incrementAndGet()), CascadeBot.LOGGER);

    private static final Pattern MULTIQUOTE_REGEX = Pattern.compile("[\"'](?=[\"'])");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        MDC.put("Guild", event.getGuild().toString());
        MDC.put("Sender", event.getAuthor().toString());
        MDC.put("Shard", event.getJDA().getShardInfo().getShardString());
        MDC.put("Channel", event.getChannel().toString());

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

        String prefix = guildData.getPrefix();
        boolean isMention = false;

        String commandWithArgs;
        String trigger;
        String[] args;

        if (message.startsWith(prefix)) {
            commandWithArgs = message.substring(prefix.length()); // Remove prefix from command
        } else if (guildData.getSettings().isMentionPrefix() && message.startsWith(event.getJDA().getSelfUser().getAsMention())) {
            commandWithArgs = message.substring(event.getJDA().getSelfUser().getAsMention().length()).trim();
            isMention = true;
        } else if (message.startsWith(Config.INS.getDefaultPrefix() + "prefix") && !Config.INS.getDefaultPrefix().equals(guildData.getPrefix())) {
            commandWithArgs = message.substring(Config.INS.getDefaultPrefix().length());
        } else {
            return;
        }

        MDC.put("Prefix", prefix);
        MDC.put("Mention tag enabled", String.valueOf(isMention));

        trigger = commandWithArgs.split(" ")[0];
        commandWithArgs = commandWithArgs.substring(trigger.length()).trim();
        args = splitArgs(commandWithArgs);

        MDC.put("Trigger", trigger);
        MDC.put("Args", Arrays.toString(args));

        try {
            processCommands(event, guildData, trigger, args, isMention);
        } catch (Exception e) {
            Messaging.sendExceptionMessage(
                    event.getChannel(),
                    "There was an error while processing your command!",
                    e);
            return;
        } finally {
            MDC.clear();
        }
    }

    public String[] splitArgs(String input) {
        // Allow ' and " to be treated equally #quoteshavefeelingstoo
        input = input.replace("'", "\"");
        boolean inQuotes = false; // Whether the current position is surrounded by quotes or not
        int splitFrom = 0; // We initially start the first split from 0 to the first space
        var args = new ArrayList<String>();
        for (int pos = 0; pos < input.length(); pos++) {
            char charAtPos = input.charAt(pos);
            if (charAtPos == ' ') {
                int splitTo = pos;
                // If there is a quote to close this
                if (inQuotes && (input.substring(pos).contains("\""))) {
                    continue;
                }
                if (input.charAt(pos - 1) == '"') {
                    splitTo = pos - 1; // If we are splitting after a quote, don't include the quote in the split
                }
                args.add(input.substring(splitFrom, splitTo));
                splitFrom = pos + 1; // Set the next split start to be after
            } else if (pos == input.length() - 1) {
                int splitTo = input.length();
                // If the end character is a quote, we want to "split" before the quote to no include it.
                if (input.charAt(pos) == '"') {
                    splitTo = pos;
                }
                args.add(input.substring(splitFrom, splitTo));
                // End of string so do nothing else
            } else if (charAtPos == '"') {
                if (!inQuotes && (pos == 0 || input.charAt(pos - 1) == ' ')) {
                    splitFrom += 1; // Start the split after the first quote
                }
                inQuotes = !inQuotes;
            }
        }
        return args.toArray(String[]::new);
    }

    private void processCommands(GuildMessageReceivedEvent event, GuildData guildData, String trigger, String[] args, boolean isMention) {
        ICommandMain cmd = CascadeBot.INS.getCommandManager().getCommand(trigger, event.getAuthor(), guildData);
        if (cmd != null) {
            if (!cmd.getModule().isFlagEnabled(ModuleFlag.PRIVATE) &&
                    !guildData.isModuleEnabled(cmd.getModule())) {
                if (guildData.getSettings().willDisplayModuleErrors() || Environment.isDevelopment()) {
                    EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
                    builder.setDescription(String.format("The module `%s` for command `%s` is disabled!", cmd.getModule().toString(), trigger));
                    builder.setTimestamp(Instant.now());
                    builder.setFooter("Requested by " + event.getAuthor().getAsTag(), event.getAuthor().getEffectiveAvatarUrl());
                    Messaging.sendDangerMessage(event.getChannel(), builder, guildData.getSettings().useEmbedForMessages());
                }
                // TODO: Modlog?
                return;
            }
            CommandContext context = new CommandContext(
                    event.getJDA(),
                    event.getChannel(),
                    event.getMessage(),
                    event.getGuild(),
                    guildData,
                    args,
                    event.getMember(),
                    trigger,
                    isMention
            );
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
        }
    }

    private boolean processSubCommands(ICommandMain cmd, String[] args, CommandContext parentCommandContext) {
        for (ICommandExecutable subCommand : cmd.getSubCommands()) {
            if (subCommand.command().equalsIgnoreCase(args[0])) {
                CommandContext subCommandContext = new CommandContext(
                        parentCommandContext.getJDA(),
                        parentCommandContext.getChannel(),
                        parentCommandContext.getMessage(),
                        parentCommandContext.getGuild(),
                        parentCommandContext.getData(),
                        ArrayUtils.remove(args, 0),
                        parentCommandContext.getMember(),
                        parentCommandContext.getTrigger() + " " + args[0],
                        parentCommandContext.isMention()
                );
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
            MDC.put("Sender", context.getMember().toString());
            MDC.put("Guild", context.getGuild().toString());
            MDC.put("Channel", context.getChannel().toString());
            MDC.put("Shard", context.getJDA().getShardInfo().getShardString());

            CascadeBot.LOGGER.info("{}Command {}{} executed by {} with args: {}",
                    (command instanceof ICommandMain ? "" : "Sub"),
                    command.command(),
                    (command.command().equalsIgnoreCase(context.getTrigger()) ? "" : " (Trigger: " + context.getTrigger() + ")"),
                    context.getUser().getAsTag(),
                    Arrays.toString(context.getArgs()));
            try {
                command.onCommand(context.getMember(), context);
            } catch (Exception e) {
                context.getTypedMessaging().replyException("There was an error running the command!", e);
                CascadeBot.LOGGER.error("Error while running a command!", MDCException.from(e));
            } finally {
                MDC.clear();
            }
        });
        deleteMessages(command, context);
        return true;
    }

    private boolean isAuthorised(ICommandExecutable command, CommandContext context) {
        if (!CascadeBot.INS.getPermissionsManager().isAuthorised(command, context.getData(), context.getMember())) {
            if (!(command instanceof ICommandRestricted)) { // Always silently fail on restricted commands, users shouldn't know what the commands are
                if (context.getSettings().willShowPermErrors()) {
                    context.getTypedMessaging().replyDanger("You don't have the permission `%s` to run this command!", command.getPermission().getPermissionNode());
                }
            }
            return false;
        }
        return true;
    }

    private void deleteMessages(ICommandExecutable command, CommandContext context) {
        if (context.getSettings().willDeleteCommand() && command.deleteMessages()) {
            if (context.getGuild().getSelfMember().hasPermission(context.getChannel(), Permission.MESSAGE_MANAGE)) {
                context.getMessage().delete().queue();
            } else {
                context.getGuild().getOwner().getUser().openPrivateChannel().queue(channel -> channel.sendMessage(
                        "We can't delete guild messages as we won't have the permission manage messages! Please either give me this " +
                                "permission or turn off command message deletion!"
                ).queue(), exception -> {
                    // Sad face :( We'll just let them suffer in silence.
                });
            }
        }
    }

    public static void shutdownCommandPool() {
        COMMAND_POOL.shutdown();
    }


}
