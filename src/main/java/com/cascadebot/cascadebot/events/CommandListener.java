/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.Config;
import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.objects.GuildData;
import com.cascadebot.cascadebot.utils.objects.ThreadPoolExecutorLogged;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

public class CommandListener extends ListenerAdapter {

    private static final ThreadGroup COMMAND_THREADS = new ThreadGroup("Command Threads");
    private static final ExecutorService COMMAND_POOL = ThreadPoolExecutorLogged.newFixedThreadPool(5, r ->
            new Thread(COMMAND_THREADS, r, "Command Pool-" + COMMAND_THREADS.activeCount()));

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = Constants.MULTISPACE_REGEX.matcher(event.getMessage().getContentRaw()).replaceAll(" ");
        String prefix = Config.VALUES.defaultPrefix; //TODO: Add guild data prefix here
        GuildData guildData = new GuildData(event.getGuild().getIdLong());
        if (message.startsWith(prefix)) {
            String commandWithArgs = message.substring(prefix.length()); // Remove prefix from command
            String trigger = commandWithArgs.split(" ")[0]; // Get first string before a space
            String[] args = ArrayUtils.remove(commandWithArgs.split(" "), 0); // Remove the command portion of the string

            ICommand cmd = CascadeBot.instance().getCommandManager().getCommand(trigger, event.getAuthor(), guildData);
            if (cmd != null) {
                CommandContext context = new CommandContext(
                        event.getChannel(),
                        event.getMessage(),
                        event.getGuild(),
                        guildData,
                        args,
                        event.getMember(),
                        trigger,
                        false
                );
                dispatchCommand(cmd, context);
            }
        } else if (guildData.isMentionPrefix() && message.startsWith(event.getJDA().getSelfUser().getAsMention())) {

            String commandWithArgs = message.substring(event.getJDA().getSelfUser().getAsMention().length()).trim();
            String trigger = commandWithArgs.split(" ")[0];
            String[] args = ArrayUtils.remove(commandWithArgs.split(" "), 0);

            ICommand cmd = CascadeBot.instance().getCommandManager().getCommand(trigger, event.getAuthor(), guildData);
            if (cmd != null) {
                CommandContext context = new CommandContext(
                        event.getChannel(),
                        event.getMessage(),
                        event.getGuild(),
                        guildData,
                        args,
                        event.getMember(),
                        trigger,
                        true
                );
                dispatchCommand(cmd, context);
            }
        } else {

        }
    }

    private void dispatchCommand(final ICommand command, final CommandContext context) {
        COMMAND_POOL.submit(() -> {
            CascadeBot.logger.info("Command {} executed by {} with args: {}",
                    command.defaultCommand() + (command.defaultCommand().equalsIgnoreCase(context.getTrigger()) ? "" : context.getTrigger()),
                    context.getUser().getName() + "#" + context.getUser().getDiscriminator(), // TODO: Util this
                    Arrays.toString(context.getArgs()));
            try {
                command.onCommand(context.getMember(), context);
            } catch (Exception e) {
                CascadeBot.logger.error(String.format(
                        "Error in command %s Guild ID: %s User: %s",
                        command.defaultCommand(), context.getGuild().getId(), context.getMember().getEffectiveName()
                ), e);
            }
        });
    }

    public static void shutdownCommandPool() {
        COMMAND_POOL.shutdown();
    }


}
