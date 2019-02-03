/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.commandmeta.IMainCommand;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.shared.Regex;
import com.cascadebot.shared.utils.ThreadPoolExecutorLogged;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

public class CommandListener extends ListenerAdapter {

    private static final ThreadGroup COMMAND_THREADS = new ThreadGroup("Command Threads");
    private static final ExecutorService COMMAND_POOL = ThreadPoolExecutorLogged.newFixedThreadPool(5, r ->
            new Thread(COMMAND_THREADS, r, "Command Pool-" + COMMAND_THREADS.activeCount()), CascadeBot.logger);

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String message = Regex.MULTISPACE_REGEX.matcher(event.getMessage().getContentRaw()).replaceAll(" ");
        GuildData guildData = GuildDataMapper.getGuildData(event.getGuild().getIdLong());

        String prefix = guildData.getCommandPrefix();
        boolean isMention = false;

        String commandWithArgs;
        String trigger;
        String[] args;

        if (message.startsWith(prefix)) {
            commandWithArgs = message.substring(prefix.length()); // Remove prefix from command
            trigger = commandWithArgs.split(" ")[0]; // Get first string before a space
            args = ArrayUtils.remove(commandWithArgs.split(" "), 0); // Remove the command portion of the string
        } else if (guildData.isMentionPrefix() && message.startsWith(event.getJDA().getSelfUser().getAsMention())) {
            commandWithArgs = message.substring(event.getJDA().getSelfUser().getAsMention().length()).trim();
            trigger = commandWithArgs.split(" ")[0];
            args = ArrayUtils.remove(commandWithArgs.split(" "), 0);
            isMention = true;
        } else {
            return;
        }

        processCommands(event, guildData, trigger, args, isMention);
    }

    private void processCommands(GuildMessageReceivedEvent event, GuildData guildData, String trigger, String[] args, boolean isMention) {
        IMainCommand cmd = CascadeBot.INS.getCommandManager().getCommand(trigger, event.getAuthor(), guildData);
        if (cmd != null) {
            CommandContext context = new CommandContext(
                    event.getChannel(),
                    event.getMessage(),
                    event.getGuild(),
                    guildData,
                    args,
                    event.getMember(),
                    trigger,
                    isMention
            );
            if (args.length >= 1) {
                if (processSubCommands(cmd, args, context)) {
                    return;
                }
            }
            dispatchCommand(cmd, context);
        }
    }

    private boolean processSubCommands(IMainCommand cmd, String[] args, CommandContext parentCommandContext) {
        for (ICommandExecutable subCommand : cmd.getSubCommands()) {
            if (subCommand.command().equalsIgnoreCase(args[0])) {
                CommandContext subCommandContext = new CommandContext(
                        parentCommandContext.getChannel(),
                        parentCommandContext.getMessage(),
                        parentCommandContext.getGuild(),
                        parentCommandContext.getData(),
                        ArrayUtils.remove(args, 0),
                        parentCommandContext.getMember(),
                        parentCommandContext.getTrigger() + " " + args[0],
                        parentCommandContext.isMention()
                );
                return dispatchCommand(subCommand, subCommandContext);
            }
        }
        return false;
    }

    private boolean dispatchCommand(final ICommandExecutable command, final CommandContext context) {
        if (!CascadeBot.INS.getPermissionsManager().isAuthorised(command, context.getData(), context.getMember())) {
            if (!(command instanceof ICommandRestricted)) { // Always silently fail on restricted commands, users shouldn't know what the commands are
                if (context.getData().willDisplayPermissionErrors()) {
                    context.replyDanger("You don't have the permission `%s` to run this command!", command.getPermission().getPermissionNode());
                }
            }
            return false;
        }
        COMMAND_POOL.submit(() -> {
            CascadeBot.logger.info("{}Command {}{} executed by {} with args: {}",
                    (command instanceof IMainCommand ? "" : "Sub"),
                    command.command(),
                    (command.command().equalsIgnoreCase(context.getTrigger()) ? "" : " (Trigger: " + context.getTrigger() + ")"),
                    context.getUser().getAsTag(),
                    Arrays.toString(context.getArgs()));
            try {
                command.onCommand(context.getMember(), context);
            } catch (Exception e) {
                CascadeBot.logger.error(String.format(
                        "Error in command %s Guild ID: %s User: %s",
                        command.command(), context.getGuild().getId(), context.getMember().getEffectiveName()
                ), e);
            }
        });
        return true;
    }

    public static void shutdownCommandPool() {
        COMMAND_POOL.shutdown();
    }


}
